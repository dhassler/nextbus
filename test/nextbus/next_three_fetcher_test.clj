(ns nextbus.next-three-fetcher-test
  (:require [clojure.test :refer :all]
            [nextbus.next-three-fetcher :refer :all]
            [net.cgrand.enlive-html :as html]
            [clojure.pprint]))

(deftest test-parse
  (testing "get rows"
    (with-redefs [fetch-url (fn [_] (test-html-data))]
      (let [rows (get-rows (fetch-url ""))]
        (is (= (count rows) 11)))))

  (testing "process row"
    (with-redefs [fetch-url (fn [_] (test-html-data))]
      (let [rows (get-rows (fetch-url ""))
            res  (process-row (first rows))
            row  (first res)]
        (is (= "225D" (:route row)))
        (is (= "14th/Walnut (Sb)" (:destination row)))
        (is (= "09:29" (:time row))))))

  (testing "all rows"
    (with-redefs [fetch-url (fn [_] (test-html-data))]
      (clojure.pprint/pprint (get-buses "14th")))))

