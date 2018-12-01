(ns nextbus.next-three-fetcher
  (:require [clojure.data.json :as json]
            [java-time :as jt]))

(def rtd-json-url "https://www.rtd-denver.com/api/nextride/stops/")

(defn format-time [unixtime]
  (let [d (->
            unixtime
            (* 1000)
            (jt/instant)
            (jt/local-date-time "America/Denver"))]
    (jt/format "hh:mm" d)))

(defn fetch-json-data [id]
  (slurp (str rtd-json-url id)))

(defn process-json [json-string headsign-filters]
  (let [parsed-json (json/read-str json-string :key-fn keyword)
        child-stops (get-in parsed-json [:data :attributes :childStops])
        route-patterns (flatten (map :routePatterns child-stops))
        trip-stops (flatten (map :tripStops route-patterns))
        buses (map #(select-keys % [:trip_headsign :scheduled_departure_time :route_short_name]) trip-stops)
        filter-fn (partial contains? headsign-filters)
        filtered-buses (filter #(filter-fn (:trip_headsign %)) buses)
        buses-with-date (map #(assoc % :time (format-time (:scheduled_departure_time %))) filtered-buses)
        final-buses (sort-by :scheduled_departure_time buses-with-date)]
    final-buses))

(defn get-buses-json [id dest-filters]
 (process-json (fetch-json-data id) dest-filters))
