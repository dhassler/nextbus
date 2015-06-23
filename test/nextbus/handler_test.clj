(ns nextbus.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [nextbus.handler :refer :all]
            [nextbus.stub-fetcher :as s]))

(use-fixtures :once s/stub-http-response)

(deftest test-app
  (testing "index route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (.contains (:body response) "Lafayette"))))

  (testing "Boulder route"
    (let [response (app (mock/request :get "/b"))]
      (is (= (:status response) 200))
      (is (.contains (:body response) "Boulder"))))

  (testing "Lafayette route"
    (let [response (app (mock/request :get "/l"))]
      (is (= (:status response) 200))
      (is (.contains (:body response) "Lafayette"))))

  (testing "not-found destination"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid/route"))]
      (is (= (:status response) 404)))))
