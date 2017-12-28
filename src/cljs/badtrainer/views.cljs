(ns badtrainer.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [monet.canvas :as canvas]))

(defn- mouse-pos-on-field [canvas x y]
  (let [rect (.getBoundingClientRect canvas)]
    [(- x (.-left rect)) (- y (.-top rect))]))

(defn clear-canvas [ctx canvas]
  (canvas/clear-rect ctx {:x 0 :y 0 :w (.-width canvas) :h (.-height canvas)})
  ctx)

(defn- update-cursor-pos [event]
  (let [x (.-clientX event)
        y (.-clientY event)]
    (re-frame/dispatch [:set-cursor-pos [x y]])))

(defn- event-pos [event]
  (let [ts (.-changedTouches event)
        t (aget ts (dec (.-length ts)))]
    [(.-clientX t) (.-clientY t)]))

(defn- draw-measures [ctx x y]
  (-> ctx
      (canvas/stroke-style "#000000")
      (canvas/stroke-width 0.5)
      (canvas/begin-path)
      (canvas/move-to x y)
      (canvas/line-to 0 y)
      (canvas/move-to x y)
      (canvas/line-to x 0)
      (canvas/stroke)))

;; components

(defn rulers-inner [x y width height]
  (reagent/create-class
   {:display-name :playing-field

    :component-did-update
    (fn [this [_ x y]]
      (let [canvas (reagent/dom-node this)
            [x-on-c y-on-c] (mouse-pos-on-field canvas x y)
            ctx (canvas/get-context canvas "2d")]
        (clear-canvas ctx canvas)
        (draw-measures ctx x-on-c y-on-c)))

    :reagent-render
    (fn [x y width height]
      [:canvas.field
       {:on-mouse-move #(update-cursor-pos %)
        :width width
        :height height
        :style {:height "100%" :width "100%" :padding 0 :margin 0 :position "absolute"}}])}))

(defn rulers-outer [width height]
  (let [[x y] @(re-frame/subscribe [:cursor-pos])]
    [rulers-inner x y width height]))

(defn point-marker [[x y] color]
  [:circle {:cx x :cy y :r 10 :fill color}])

(defn point-markers []
  (let [points @(re-frame/subscribe [:points])
        markers (map (fn [{:keys [end-pos]}] [point-marker end-pos "green"]) points)]
    (into [:svg {:width "100%" :height "100%" :style {:position "fixed" :top 0 :left 0}}]
          markers)))

(defn current-tracker []
  (let [[x1 y1 :as start] @(re-frame/subscribe [:current-start])
        [x2 y2] @(re-frame/subscribe [:current-track])]
    [:svg {:width "100%" :height "100%" :style {:position "fixed" :top 0 :left 0}}
     [:circle {:cx x1 :cy y1 :r 10 :fill "black"}]
     [:line {:x1 x1 :y1 y1 :x2 x2 :y2 y2 :stroke "black" :stroke-width "5"}]]))

(defn field []
  [:svg {:width "100%" :height "100%" :viewBox "0 0 630 1350"
         :style {:margin-top "30px" :margin-bottom "20px" :position "fixed" :top 0 :left 0}}
   [:rect {:x "10" :y "10" :width "610" :height "1330" :fill "none" :stroke-width "10" :stroke "white"}]
   [:line {:x1 "55" :y1 "10" :x2 "55" :y2 "1340" :stroke "white" :stroke-width "10"} ]
   [:line {:x1 "575" :y1 "10" :x2 "575" :y2 "1340" :stroke "white" :stroke-width "10"}]
   [:line {:x1 "10" :y1 "477" :x2 "620" :y2 "477" :stroke "white" :stroke-width "10"}]
   [:line {:x1 "10" :y1 "82" :x2 "620" :y2 "82" :stroke "white" :stroke-width "10"}]
   [:line {:x1 "10" :y1 "1268" :x2 "620" :y2 "1268" :stroke "white" :stroke-width "10"}]
   [:line {:x1 "10" :y1 "873" :x2 "620" :y2 "873" :stroke "white" :stroke-width "10"}]
   [:line {:x1 "300" :y1 "10" :x2 "300" :y2 "472" :stroke "white" :stroke-width "10"}]
   [:line {:x1 "300" :y1 "868" :x2 "300" :y2 "1340" :stroke "white" :stroke-width "10"}]
   [:line {:x1 "10" :y1 "675" :x2 "620" :y2 "675" :stroke "white" :stroke-width "10"}]])

(defn row [id]
  [:div {:id id :style {:width "100%" :height "calc(100vh / 8)" :display :flex :flex-direction :row}}
   [:div {:style {:border "1px solid black" :width "calc(100vw / 5)"}} "out-left"]
   [:div {:style {:border "1px solid black" :width  "calc(100vw / 5)"}} "left"]
   [:div {:style {:border "1px solid black" :width  "calc(100vw / 5)"}} "center"]
   [:div {:style {:border "1px solid black" :width  "calc(100vw / 5)"}} "right"]
   [:div {:style {:border "1px solid black" :width  "calc(100vw / 5)"}} "out-right"]])

(defn field2 []
  [:div {:style {:width  "100%" :display :flex :flex-direction :column}}
   (for [pos [:away-out :away-back :away-mid :away-fore
              :home-fore :home-mid :home-back :home-out]]
     ^{:key pos}
     [row pos])])

;; game-panel

(defn game-panel []
  (let [points (re-frame/subscribe [:points])
        width 750
        height 1334]
    (fn []
      [:div
       {:style {:width "100%" :height "100%"}
        :on-touch-start #(re-frame/dispatch [:start-point-tracking (event-pos %)])
        :on-touch-move #(re-frame/dispatch [:track-current-point (event-pos %)])
        :on-touch-end #(re-frame/dispatch [:store-point (event-pos %)])}
       [field2]
       [field]
       [current-tracker]
       [point-markers]
       #_[rulers-outer width height]])))

(defn main-panel []
  [game-panel])
