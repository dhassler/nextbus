(ns nextbus.next-three-fetcher-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [nextbus.next-three-fetcher :as f]
            [nextbus.stub-fetcher :as s]))

(use-fixtures :once s/stub-http-response)

(deftest test-parse
  (testing "validate data"
  (let [json-string (f/fetch-json-data 33700)
        parsed-json (json/read-str json-string :key-fn keyword)
        child-stops (get-in parsed-json [:data :attributes :childStops])
        route-patterns (flatten (map :routePatterns child-stops))
        trip-stops (flatten (map :tripStops route-patterns))
        buses (map #(select-keys % [:direction_name :trip_headsign :scheduled_departure_time :route_short_name]) trip-stops)
        westbound (filter #(= (:direction_name %) "E-Bound") buses)
        headsigns (map #(select-keys % [:route_short_name :trip_headsign]) westbound)]
    (println headsigns)
    (is (= 1 1))
    )))

