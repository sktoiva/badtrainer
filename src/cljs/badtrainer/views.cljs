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

(defn playing-field []
  [:div {:style {:padding "10px" :width "100%" :height "100%"}}
   [:div {:style {:height "50%" :width "100%" :border "5px solid white" :position "fixed" :top 0 :left 0 :z-index 10}
          :on-touch-end #(re-frame/dispatch [:opponent-point (event-pos %)])}]
   [:div {:style {:height "50%" :width "100%" :border "5px solid white" :position "fixed" :top "50%" :left 0 :z-index 10}
          :on-touch-end #(re-frame/dispatch [:home-point (event-pos %)])}]])

(defn point-marker [[x y] color]
  (fn [[x y]]
    [:circle {:cx x :cy y :r 10 :fill color}]))

(defn point-markers []
  (let [home-points @(re-frame/subscribe [:home-points])
        opponent-points @(re-frame/subscribe [:opponent-points])
        home-markers (map (fn [hp] [point-marker hp "green"]) home-points)
        opponent-markers (map (fn [op] [point-marker op "red"]) opponent-points)]

    (into [:svg {:width "100%" :height "100%" :style {:position "fixed" :top 0 :left 0 :z-index 1}}]
          (into [home-markers] opponent-markers))))


;; game-pane

(defn game-panel []
  (let [points (re-frame/subscribe [:points])
        width 750
        height 1334]
    (fn []
      [:div {:style {:width "100%" :height "100%"}}
       [playing-field]
       [point-markers]
       #_[rulers-outer width height]])))

(defn main-panel []
  [game-panel])
