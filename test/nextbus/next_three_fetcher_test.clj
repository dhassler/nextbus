(ns nextbus.next-three-fetcher-test
  (:require [clojure.test :refer :all]
            [nextbus.next-three-fetcher :as f]
            [nextbus.stub-fetcher :as s]))

(use-fixtures :once s/stub-http-response)

(deftest test-parse
  (testing "get rows"
      (let [rows (f/get-rows (f/fetch-mystop 25903))]
        (is (= (count rows) 7))))

  (testing "process row"
      (let [rows (f/get-rows (f/fetch-mystop 25903))
            res  (f/process-row (first rows))
            row  (first res)]
        (is (= "225D" (:route row)))
        (is (= "14th/Walnut (Sb)" (:destination row)))
        (is (= "14:27" (:time row))))))
