(ns nextbus.next-three-fetcher
  (:require [net.cgrand.enlive-html :as html]
            [clojure.core.async :refer [>! <!! chan go]]
            [clojure.string :as s]))

(def rtd-url "http://m.rtd-denver.com/mobi/getMyStop.do?stopId=")

(defn fetch-mystop [mystop-id]
  (html/html-resource (java.net.URL. (str rtd-url mystop-id))))

; Extract route - second element in tr list
; {:tag :th,
; :attrs {:bgcolor "#f4f4f4", :scope "row", :class "leftcol"},
; :content
; ({:tag :a,
;      :attrs
;      {:href "getSchedulesFormForChosenRoute.do?routeId=225&type=bus"},
;      :content ("225D")})}
(defn extract-route [th]
  (let [c (:content th)
        f (first c)
        c2 (:content f)]
    (first c2)))

; {:tag :td,
;   :attrs {:bgcolor "#f4f4f4"},
;   :content ("14th/Walnut (Sb)")}
(defn extract-first-content [td]
    (s/replace (first (:content td)) #"[\n\t\r]" ""))

(defn format-time [s]
  (let [z (s/replace (s/trim s) #"^\d:" #(str "0" %1))]
    (if (.endsWith z "a")
      (s/replace z "a" "")
      (s/replace (s/replace (s/replace z #"^\d+" #(str (+ 12 (Integer/parseInt %1)))) "p" "") #"^24" "12"))))

(defn row-to-hash [row time-idx]
  (-> {}
      (assoc :route       (extract-route         (nth row 1)))
      (assoc :destination (extract-first-content (nth row 3)))
      (assoc :time        (format-time (extract-first-content (nth row time-idx))))))

(defn process-row [row] [(row-to-hash row 5) (row-to-hash row 7)])

(defn get-rows [html]
  (let [trs (html/select html [:table :tbody :tr])
        cs  (map :content trs)]
    (filter #(> (count %) 4) cs)))

(defn parallel-fetch [ids]
  (let [http-channel (chan)
        res          (atom [])]

    (doseq [id ids]
      (go (>! http-channel (fetch-mystop id))))

    (doseq [id ids]
      (swap! res conj (<!! http-channel)))

    (mapcat process-row (mapcat get-rows @res))))

(defn get-buses [ids dest-filter-string]
  (let [regex (re-pattern (str ".*" dest-filter-string ".*"))]
    (->>
      (parallel-fetch ids)
      (filter #(.contains (:time %) ":"))
      (filter #(re-matches regex (:destination %)))
      (sort-by :time))))
