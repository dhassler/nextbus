(ns nextbus.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [nextbus.next-three-fetcher :refer [get-buses get-buses-test]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            (hiccup page core form)))

(def origin-map { "b" {:label "Boulder", :ids [33236,24591,34281], :filter ""},
                    "l" {:label "Lafayette", :ids [25903], :filter "14th"}})


(defn bus-row [h]
  (hiccup.core/html [:tr
                     [:td (:time h)]
                     [:td (:route h)]
                     [:td (:destination h)]]))

; (if (re-matches #".*(y|r).*" "Boulder") "Yes" "No")

(defn index [origin]
  (hiccup.page/html5
    {:lang "en"}
    [:head (hiccup.page/include-css "style.css")]
    [:body [:div [:h1 {:class "info"} (:label origin)]
            [:h4 {:id "current-time"}]
            [:table (map bus-row (take 5 (get-buses (:ids origin) (:filter origin))))]
            [:button {:class "reload" :type "button" :onClick "location.reload(true)"} [:h2 "Reload"]]]
     (hiccup.page/include-js "nextbus.js")]))


(defroutes app-routes
  (GET "/" [] (index (origin-map "b")))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
