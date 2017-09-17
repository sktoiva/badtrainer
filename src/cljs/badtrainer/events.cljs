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
 :home-point
 (fn [db [_ pos]]
   (update db :home-points (fn [val]
                                 (if (some? val)
                                   (conj val pos)
                                   [pos])))))

(re-frame/reg-event-db
 :opponent-point
 (fn [db [_ pos]]
   (update db :opponent-points (fn [val]
                                 (if (some? val)
                                   (conj val pos)
                                   [pos])))))

(re-frame/reg-event-db
 :set-cursor-pos
 (fn [db [_ pos]]
   (assoc db :cursor-pos pos)))
