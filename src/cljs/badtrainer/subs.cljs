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
 :current-point
 (fn [db]
   (:current-point db)))

(re-frame/reg-sub
 :points
 (fn [db]
   (:points db)))

(re-frame/reg-sub
 :current-start
 :<- [:current-point]
 (fn [{:keys [start-pos]} _]
   start-pos))

(re-frame/reg-sub
 :current-track
 :<- [:current-point]
 (fn [{:keys [current-pos]}]
   current-pos))
