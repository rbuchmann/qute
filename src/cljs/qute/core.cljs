(ns qute.core
  (:require
   [cljs.pprint :refer [pprint]]
   [reagent.core :as reagent]
   [reagent.dom :refer [render]]
   [qute.simulation :as sim]
   [qute.visualization :as vis]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce app-state
  (reagent/atom {:event-delay     20
                 :event-size      20
                 :random-delay    0
                 :random-size     0
                 :processing-time 1}
                ))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(defn slider [k min max]
  [:input {:type :range
           :min min
           :max max
           :value (get @app-state k)
           :on-change (fn [evt] (swap! app-state assoc
                                     k
                                     (-> evt .-target .-value js/parseInt)))}])

(defn event-fn []
  (let [{:keys [event-delay random-delay
                event-size random-size]} @app-state]
    [(max 1 (+ event-delay (* (- (.random js/Math) 0.5) random-delay)))
     (max 1 (+ event-size  (* (- (.random js/Math) 0.5) random-size)))]))

(defn page [ratom]
  (let [{:keys [processing-time]} @app-state
        simulation (sim/simulate-events
                    200
                    event-fn
                    processing-time)]
    [:div
     [:h1
      "Let's queue some eventssss!"]

     [:div
      [vis/visualize-simulation [400 200] simulation]]
     [:h2 "Event delay"]
     [slider :event-delay 1 50]
     [:h2 "Payload size"]
     [slider :event-size 1 50]
     [:h2 "Processing time"]
     [slider :processing-time 1 3]
     [:h2 "Delay randomness"]
     [slider :random-delay 0 50]
     [:h2 "Payload size randomness"]
     [slider :random-size 0 50]
     [:pre (with-out-str
             (pprint @app-state))]]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")
    ))

(defn reload []
  (render [page app-state]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (reload))
