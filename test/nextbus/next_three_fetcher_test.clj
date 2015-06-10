(ns nextbus.next-three-fetcher-test
  (:require [clojure.test :refer :all]
            [nextbus.next-three-fetcher :refer :all]
            [net.cgrand.enlive-html :as html]
            [clojure.pprint]))

(def test-html-data
  (html/html-resource "get-my-stop.html"))

(deftest test-parse
  (testing "get rows"
    (with-redefs [fetch-url (fn [_] test-html-data)]
      (let [rows (get-rows (fetch-url ""))]
        (is (= (count rows) 11)))))

  (testing "process row"
    (with-redefs [fetch-url (fn [_] test-html-data)]
      (let [rows (get-rows (fetch-url ""))
            row  (process-row (first rows))]
        (is (= "225D" (:route row)))
        (is (= "14th/Walnut (Sb)" (:destination row)))
        (is (= "09:29a" (:first_time row)))
        (is (= "10:29a" (:second_time row)))
    )))

  (testing "all rows"
    (with-redefs [fetch-url (fn [_] test-html-data)]
      (clojure.pprint/pprint (get-buses "14th")))))

