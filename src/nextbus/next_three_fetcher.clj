(ns nextbus.next-three-fetcher
  (:require [clojure.data.json :as json]
            [clojure.core.async :refer [>! <!! chan go]]
            [java-time :as jt]
            [clj-http.client :as client]))

(def rtd-json-url "https://www.rtd-denver.com/api/nextride/stops/")

(defn format-time [unixtime]
  (let [d (->
            unixtime
            (* 1000)
            (jt/instant)
            (jt/local-date-time "America/Denver"))]
    (jt/format "hh:mm" d)))

(defn fetch-json-data [id]
  (:body (client/get (str rtd-json-url id) {:accept :json})))

;; handles having child stops, or not
(defn route-patterns-from-attributes [attrs]
  (if (< 0 (count(:routePatterns attrs)))
    (:routePatterns attrs)
    (flatten (map :routePatterns (:childStops attrs))) ;; recurse over child stops
  ))

(defn process-json [json-string headsign-filters]
  (let [parsed-json (json/read-str json-string :key-fn keyword)
        attributes (get-in parsed-json [:data :attributes])
        route-patterns (route-patterns-from-attributes attributes)
        trip-stops (flatten (map :tripStops route-patterns))
        buses (map #(select-keys % [:trip_headsign :scheduled_departure_time :route_short_name :trip_status]) trip-stops)
        filter-fn (partial contains? headsign-filters)
        filtered-buses (filter #(filter-fn (:trip_headsign %)) buses)
        filtered-buses2 (filter #(not= (:trip_status %) "CANCELLED") filtered-buses)
        buses-with-date (map #(assoc % :time (format-time (:scheduled_departure_time %))) filtered-buses2)]
    buses-with-date))

(defn get-buses-json [id dest-filters]
 (process-json (fetch-json-data id) dest-filters))

(defn get-buses-json-multi [ids dest-filters]
  (let [fetch-channel (chan)
        result (atom [])]

    (doseq [id ids]
      (go (>! fetch-channel (get-buses-json id dest-filters))))

    (doseq [id ids]
      (swap! result conj (<!! fetch-channel)))

    (sort-by :scheduled_departure_time (flatten @result))))
