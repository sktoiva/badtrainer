(ns badtrainer.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 :home-points
 (fn [db]
   (:home-points db)))

(re-frame/reg-sub
 :opponent-points
 (fn [db]
   (:opponent-points db)))

(re-frame/reg-sub
 :points
 :<- [:home-points]
 :<- [:opponent-points]
 (fn [[home-points opponent-points] _]
   {:home (count home-points)
    :opponent (count opponent-points)}))

(re-frame/reg-sub
 :cursor-pos
 (fn [db]
   (:cursor-pos db)))
