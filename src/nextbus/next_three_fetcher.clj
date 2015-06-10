(ns nextbus.next-three-fetcher
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as s]))

(def rtd-url "http://m.rtd-denver.com/mobi/getMyStop.do?stopId=&lightRailStation=&pnr=24")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn test-html-data []
  (html/html-resource "get-my-stop.html"))

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
  (let [z (s/replace s #"^\d:" #(str "0" %1))]
    (if (.endsWith z "a")
      (s/replace z "a" "")
      (s/replace (s/replace (s/replace z #"^\d+" #(str (+ 12 (Integer/parseInt %1)))) "p" "") #"^24" "12"))))

(defn process-row [row]
  [
    (-> {}
        (assoc :route       (extract-route         (nth row 1)))
        (assoc :destination (extract-first-content (nth row 3)))
        (assoc :time        (format-time (extract-first-content (nth row 5)))))
    (-> {}
        (assoc :route       (extract-route         (nth row 1)))
        (assoc :destination (extract-first-content (nth row 3)))
        (assoc :time        (format-time (extract-first-content (nth row 7)))))
   ])

(defn get-rows [html]
  (let [trs (html/select html [:table :tbody :tr])
        cs  (map :content trs)]
    (filter #(> (count %) 4) cs)))

(defn get-buses [dest-filter-string]
  (sort-by :time
           (filter #(.contains (:time %) ":")
                   (filter #(.contains (:destination %) dest-filter-string)
                           (mapcat process-row (get-rows (fetch-url rtd-url)))))))

(defn get-buses-test [dest-filter-string]
  (sort-by :time
    (filter #(.contains (:destination %) dest-filter-string)
            (mapcat process-row (get-rows (test-html-data))))))
