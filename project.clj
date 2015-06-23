(defproject nextbus "1.0.0"
  :description "Get next buses from Lafayette PnR"
  :url "http://github.com/dhassler/nextbus"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring/ring-defaults "0.1.2"]
                 [enlive "1.1.5"]
                 [hiccup "1.0.5"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler nextbus.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
