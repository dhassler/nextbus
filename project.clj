(defproject nextbus "1.1.0"
  :description "Get next buses from Lafayette PnR"
  :url "http://github.com/dhassler/nextbus"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.1.2"]
                 [org.clojure/data.json "0.2.6"]
                 [hiccup "1.0.5"]
                 [clojure.java-time "0.3.2"]
                 [org.clojure/core.async "0.4.490"]]
  :plugins [[lein-ring "0.12.0"]]
  :ring {:handler nextbus.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
