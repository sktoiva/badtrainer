(ns badtrainer.ui.core
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
    (swap! current-hits conj {:coords [x y]})))

(defn svg-coords [[x y]]
  (let [svg (.getElementById js/document "field")
        pt (.createSVGPoint svg)
        _ (set! (.-x pt) x)
        _ (set! (.-y pt) y)
        svgPt (.matrixTransform pt (.. svg getScreenCTM inverse))]
    [(.-x svgPt) (.-y svgPt)]))

(defn draw-circle [{:keys [coords]}]
  (let [[x y] (svg-coords coords)]
    [:circle {:cx x :cy y :r "2" :stroke "gray"}]))

(defn draw-strokes [hits]
  (when (seq hits)
    (let [[x y] (-> hits first :coords svg-coords)
          path (concat ["M" x y]
                       (->> hits
                            rest
                            (map :coords)
                            (map svg-coords)
                            (mapcat (fn [[x y]] ["L" x y]))))]
      [:path {:d (clojure.string/join " " path) :fill "transparent" :stroke "gray"}])))

(rum/defc lines []
  [:path {:d "M 22 15 L 327 15 L 327 685 L 22 685 Z M 0 350 L 350 350" :fill "transparent" :stroke "black"}])

(rum/defc field-comp
  < rum/reactive
  []
  (let [chs (rum/react current-hits)]
    ;; court measurements: width 610cm (+ 45 cm outside on both sides), height 1340 cm (+30 cm outside), scaled to half
    [:svg {:id "field" :width "350px" :height "700px" :style {:border "1px solid black"}
           :on-click #(track-hits %)}
     (lines)
     (mapv draw-circle chs)
     (draw-strokes chs)]))

(rum/defc game-data < rum/reactive
  []
  (let [game-points (rum/react hits)
        current-points (rum/react current-hits)]
    [:div
     [:div "Current points: " (str current-points)]
     [:div "Game points: " (str game-points)]]))

(rum/defc end-button
  []
  [:button {:on-click (fn []
                        (swap! hits conj @current-hits)
                        (reset! current-hits []))}
   "End sequence"])

(rum/defc undo-button
  []
  [:button {:on-click (fn []
                        (when (seq @current-hits)
                          (swap! current-hits pop)))}
   "Undo"])

(rum/defc hit-type-button
  [hit-type]
  [:button {:on-click (fn []
                        (let [latest (last @current-hits)]
                          (when latest
                            (swap! current-hits
                                   #(conj (pop %)
                                          (assoc latest :type hit-type))))))}
   (clojure.string/capitalize (name hit-type))])

(rum/defc root []
  [:div
   (field-comp)
   [:br]
   (end-button)
   (undo-button)
   (hit-type-button :clear)
   (hit-type-button :smash)
   (hit-type-button :drop)
   (game-data)])

(rum/mount (root)
           (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
