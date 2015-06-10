(ns nextbus.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [nextbus.next-three-fetcher :refer [get-buses get-buses-test]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            (hiccup page core form)))

(defn bus-row [h]
  (hiccup.core/html [:tr
                     [:td (:time h)]
                     [:td (:route h)]
                     [:td (:destination h)]]))

(defn index []
  (hiccup.page/html5
    {:lang "en"}
    [:head (hiccup.page/include-css "style.css")]
    [:body [:div [:h1 {:class "info"} "Leaving Lafayette"]
            [:table (map bus-row (take 4 (get-buses-test "14th")))]
            [:button {:class "reload" :type "button" :onClick "location.reload(true)"} "Reload"]
            ]]))


(defroutes app-routes
  (GET "/" [] (index))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
