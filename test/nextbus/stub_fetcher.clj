(ns nextbus.stub-fetcher
  (:require [net.cgrand.enlive-html :as html]))

(defn load-stub-file [mystop-id]
  (html/html-resource (str "html/" mystop-id ".html")))

(defn stub-http-response [f]
  (with-redefs [nextbus.next-three-fetcher/fetch-mystop load-stub-file]
    (f)))
