(ns qute.simulation)

(def event {:start-time   0
            :wait-time    10
            :processing-time 1})

(def default-processing-time 1)

(defn total-time [{:keys [wait-time processing-time]}]
  (+ (or wait-time 0)
     (or processing-time default-processing-time)))

(defn remaining-time [last-event t-new]
  (let [delta-t (- t-new (or (:start-time last-event) 0))]
    (max 0 (- (total-time last-event)
              delta-t))))

(defn add-event [events event-fn processing-time]
  (let [[_ last-event]    (last events)
        [dt payload-size] (event-fn)
        t-new             (+ (or (:start-time last-event) 0) dt)
        wait-time         (remaining-time last-event t-new)]
    (assoc events t-new {:start-time      t-new
                         :wait-time       wait-time
                         :processing-time (* payload-size
                                             processing-time)})))

(defn iterate-while [pred f x]
  (->> (iterate f x)
       (take-while pred)
       last))

(defn simulate-events [max-time event-fn processing-time]
  (->> (sorted-map)
       (iterate-while
        (fn [event]
          (let [[_ {:keys [start-time]}] (last event)]
            (< (or start-time 0) max-time)))
        #(add-event % event-fn processing-time))
       vals))
