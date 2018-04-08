(ns badtrainer.core
    (:require [rum.core :as rum]))

(enable-console-print!)

(println "This text is printed from src/badtrainer/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(def hits (atom []))
(def current-hits (atom []))

(defn track-hits [event]
  (let [x (.-clientX event)
        y (.-clientY event)]
    (swap! current-hits conj [x y])))

(defn svg-coords [[x y]]
  (let [svg (.getElementById js/document "field")
        pt (.createSVGPoint svg)
        _ (set! (.-x pt) x)
        _ (set! (.-y pt) y)
        svgPt (.matrixTransform pt (.. svg getScreenCTM inverse))]
    [(.-x svgPt) (.-y svgPt)]))

(defn draw-circle [coords]
  (let [[x y] (svg-coords coords)]
    [:circle {:cx x :cy y :r "2" :stroke "gray"}]))

(defn draw-strokes [coords]
  (when (seq coords)
    (let [[x y] (-> coords first svg-coords)
          path (concat ["M" x y]
                       (->> coords
                            rest
                            (map svg-coords)
                            (mapcat (fn [[x y]] ["L" x y]))))]
      [:path {:d (clojure.string/join " " path) :fill "transparent" :stroke "gray"}])))

(rum/defc field-comp
  < rum/reactive
  []
  (let [coords (rum/react current-hits)]
    ;; court measurements: width 610cm (+ 45 cm outside on both sides), height 1340 cm (+30 cm outside), scaled to half
    [:svg {:id "field" :width "350px" :height "700px" :style {:border "1px solid black"}
           :on-click #(track-hits %)}
     (mapv draw-circle coords)
     (draw-strokes coords)]))

(rum/defc total-points < rum/reactive
  []
  (let [points (count (rum/react hits))]
    [:div points]))

(rum/defc end-button
  []
  [:button {:on-click (fn []
                        (swap! hits conj @current-hits)
                        (reset! current-hits []))}
   "End sequence"])

(rum/defc undo-button
  []
  [:button {:on-click (fn []
                        (swap! current-hits pop))}
   "Undo"])

(rum/defc root []
  [:div
   (field-comp)
   (end-button)
   (undo-button)
   (total-points)])

(rum/mount (root)
           (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
