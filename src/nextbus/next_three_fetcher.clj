(ns nextbus.next-three-fetcher
  (:require [net.cgrand.enlive-html :as html]))

(def rtd-url "http://m.rtd-denver.com/mobi/getMyStop.do?stopId=&lightRailStation=&pnr=24")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

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
    (clojure.string/replace (first (:content td)) #"[\n\t\r]" ""))

(defn format-time [s]
  (clojure.string/replace
    s
    #"^\d:"
    #(str "0" %1)))

(defn process-row [row]
  (-> {}
      (assoc :route       (extract-route         (nth row 1)))
      (assoc :destination (extract-first-content (nth row 3)))
      (assoc :first_time  (format-time (extract-first-content (nth row 5))))
      (assoc :second_time (format-time (extract-first-content (nth row 7))))))

(defn get-rows [html]
  (let [trs (html/select html [:table :tbody :tr])
        cs  (map :content trs)]
    (filter #(> (count %) 4) cs)))

(defn get-buses [dest-filter-string]
  (sort-by :first_time
    (filter #(.contains (:destination %) dest-filter-string)
            (map process-row (get-rows (fetch-url rtd-url))))))
