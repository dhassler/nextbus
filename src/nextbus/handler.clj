(ns nextbus.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [nextbus.next-three-fetcher :refer [get-buses get-buses-test]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.string :as s]
            (hiccup page core form)))

(def origin-map { "b" {:label "Boulder",   :ids [33236,24591,34281], :filter "(Broomfield|Lafayette)"},
                  "l" {:label "Lafayette", :ids [25903,17962],       :filter "14th"}})


(defn bus-row [h]
  (hiccup.core/html [:div {:class "row"}
                     [:div {:class "half column"} (s/replace (:time h) #"^0" "")]
                     [:div {:class "half column"} (:route h)]]))

(defn index [origin]
  (hiccup.page/html5
    {:lang "en"}
    [:head
       (hiccup.page/include-css "normalize.css")
       (hiccup.page/include-css "style.css")
       [:meta {:viewport "width=device-width, initial-scale=1"}]]
    [:body [:div {:class "wrapper"}
            [:div {:class "row"}
              [:div {:class "full column centered title"} (:label origin) [:button {:class "swap" :onclick "swap()"} "&#8634"]]]
            [:div {:class "row"}
              [:div {:class "full column centered time" :id "current-time"}]]
            (map bus-row (take 5 (get-buses (:ids origin) (:filter origin))))
            [:div {:class "row"}
              [:div {:class "full column centered"}
                [:button {:class "reload" :type "button" :onClick "location.reload(true)"} "Reload"]]]]
     (hiccup.page/include-js "nextbus.js")]))


(defroutes app-routes
  (GET "/" [] (index (origin-map "l")))
  (GET "/:dest" {{dest :dest} :params} (index (origin-map dest)))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
