(ns badtrainer.events
  (:require [re-frame.core :as re-frame]
            [badtrainer.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/reg-event-db
 :start-point-tracking
 (fn [db [_ pos]]
   (assoc db :current-point {:start-pos pos
                             :current-pos pos})))

(re-frame/reg-event-db
 :track-current-point
 (fn [db [_ pos]]
   (assoc-in db [:current-point :current-pos] pos)))

(re-frame/reg-event-db
 :store-point
 (fn [db [_ pos]]
   (let [new-point {:start-pos (get-in db [:current-point :start-pos])
                    :end-pos pos}]
     (-> db
         (update :points #(conj % new-point))
         (dissoc :current-point)))))
