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
        attributes (get-in parsed-json [:data :attributes])
        route-patterns (f/route-patterns-from-attributes attributes)
        trip-stops (flatten (map :tripStops route-patterns))
        buses (map #(select-keys % [:direction_name :trip_headsign :scheduled_departure_time :route_short_name]) trip-stops)
        westbound (filter #(= (:direction_name %) "E-Bound") buses)
        headsigns (map #(select-keys % [:route_short_name :trip_headsign]) westbound)]
    (is (= (count headsigns) 30))
    )))

(deftest test-parse-with-child-stops
  (testing "validate data"
  (let [json-string (f/fetch-json-data 33700)
        parsed-json (json/read-str json-string :key-fn keyword)
        attributes (get-in parsed-json [:data :attributes])
        route-patterns (f/route-patterns-from-attributes attributes)
        trip-stops (flatten (map :tripStops route-patterns))
        buses (map #(select-keys % [:direction_name :trip_headsign :scheduled_departure_time :route_short_name]) trip-stops)
        westbound (filter #(= (:direction_name %) "E-Bound") buses)
        headsigns (map #(select-keys % [:route_short_name :trip_headsign]) westbound)]
    ;;(println route-patterns)
    (is (= (count headsigns) 161))
    )))

(deftest test-parse-time
  (testing "date is correct tz"
    (let [unixtime 1510186144
          parsed-time (f/format-time unixtime)]
      (is (= parsed-time "05:09")))))


(deftest test-all-parsing
  (testing "end to end parsing"
    (let [dest-filters #{"US36 & Brmfld 225D Diamond Cir 225 Laf PnR" "Lafayette PnR" "US36 & Brmfld 225E EBCC 225E Laf PnR" "Lafayette PnR via Sir Galahad" "US36 & Brmfld  225D Diamond Cir  225 Laf PnR" "US36 & Broomfield"}
          output (f/process-json (f/fetch-json-data 34281) dest-filters)]
      (is (= (count output) 17)))))

