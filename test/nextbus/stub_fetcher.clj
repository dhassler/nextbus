(ns nextbus.stub-fetcher
  )

(defn load-stub-file [mystop-id]
  (slurp (str "test/html/" mystop-id ".json")))

(defn stub-http-response [f]
  (with-redefs [nextbus.next-three-fetcher/fetch-json-data load-stub-file]
    (f)))

