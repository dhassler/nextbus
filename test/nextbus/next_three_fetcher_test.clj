(ns nextbus.next-three-fetcher-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [nextbus.next-three-fetcher :as f]
            [nextbus.stub-fetcher :as s]))

(use-fixtures :once s/stub-http-response)

(deftest test-parse
  (testing "validate data"
  (let [json-string (f/fetch-json-data 34281)
        parsed-json (json/read-str json-string :key-fn keyword)
        route-patterns (get-in parsed-json [:data :attributes :routePatterns])
        trip-stops (flatten (map :tripStops route-patterns))
        buses (map #(select-keys % [:direction_name :trip_headsign :scheduled_departure_time :route_short_name]) trip-stops)
        westbound (filter #(= (:direction_name %) "E-Bound") buses)
        headsigns (map #(select-keys % [:route_short_name :trip_headsign]) westbound)]
    (println headsigns)
    (is (= 1 0))
    )))

(deftest test-parse-time
  (testing "date is correct tz"
    (let [unixtime 1510186144
          parsed-time (f/format-time unixtime)]
      (is (= parsed-time "05:09")))))
