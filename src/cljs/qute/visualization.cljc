(ns qute.visualization
  (:require [clojure.string :as str]))

(defn svg [[width height] & content]
  (into
   [:svg {:width  width
          :height height}]
   content))


(defn point-string [points]
  (->> points
       (map #(str/join "," %))
       (interpose " ")
       (apply str)))

(defn polygon [attrs & points]
  [:polygon (merge {:points (point-string points)}
                   attrs)])

(defn event->poly [{:keys [start-time wait-time processing-time]}]
  (let [end-wait-time (+ start-time wait-time)
        end-time      (+ end-wait-time processing-time)]
    [:g
     [polygon {:style {:fill   :yellow
                       :stroke :black}}
      [start-time (+ wait-time processing-time)]
      [start-time wait-time]
      [end-wait-time 0]
      [end-wait-time processing-time]]
     [polygon {:style {:fill   :blue
                       :stroke :black}}
      [end-wait-time processing-time]
      [end-wait-time 0]
      [end-time 0]]]))


(defn visualize-simulation [[width height :as size] events]
  [svg size
   (into
    [:g {:transform (str "matrix(1 0 0 -1 0 " height ")")}]
    (map event->poly events))])
