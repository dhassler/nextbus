(ns nextbus.stub-fetcher
  )

(defn load-stub-file [mystop-id]
  (slurp (str "test/html/" mystop-id ".html")))

(defn stub-http-response [f]
  (with-redefs [nextbus.next-three-fetcher/fetch-mystop-json load-stub-file]
    (f)))
