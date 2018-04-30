(ns badtrainer.ui.core
  (:require [rum.core :as rum]
            [badtrainer.ui.util.keyboard :refer [keyboard-mixin]]
            [goog.events.KeyCodes :as KeyCodes]))

(enable-console-print!)

(println "This text is printed from src/badtrainer/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(def hits (atom []))
(def current-hits (atom []))
(def shot-type (atom nil))
(def player-court (atom :upper))

(defn svg-coords [x y]
  (let [svg (.getElementById js/document "field")
        pt (.createSVGPoint svg)
        _ (set! (.-x pt) x)
        _ (set! (.-y pt) y)
        svgPt (.matrixTransform pt (.. svg getScreenCTM inverse))]
    [(.-x svgPt) (.-y svgPt)]))

(defn track-hits [event]
  (let [x (.-clientX event)
        y (.-clientY event)]
    (swap! current-hits conj {:coords (svg-coords x y) :id (random-uuid) :player-court @player-court})))

(rum/defc draw-circle < {:key-fn (fn [{:keys [id]}] id)}
  [{:keys [coords]}]
  (let [[x y] coords]
    [:circle {:cx x :cy y :r "3" :stroke "gray"}]))

(rum/defc draw-strokes [hits]
  (when (seq hits)
    (let [[x y] (-> hits first :coords)
          path (concat ["M" x y]
                       (->> hits
                            rest
                            (map :coords)
                            (mapcat (fn [[x y]] ["L" x y]))))]
      [:path {:d (clojure.string/join " " path) :fill "transparent" :stroke "gray"}])))

(rum/defc field []
  [:g
   [:rect {:x "10" :y "10" :width "610" :height "1330" :fill "none" :stroke-width "10" :stroke "black"}]
   [:line {:x1 "55" :y1 "10" :x2 "55" :y2 "1340" :stroke "black" :stroke-width "10"} ]
   [:line {:x1 "575" :y1 "10" :x2 "575" :y2 "1340" :stroke "black" :stroke-width "10"}]
   [:line {:x1 "10" :y1 "477" :x2 "620" :y2 "477" :stroke "black" :stroke-width "10"}]
   [:line {:x1 "10" :y1 "82" :x2 "620" :y2 "82" :stroke "black" :stroke-width "10"}]
   [:line {:x1 "10" :y1 "1268" :x2 "620" :y2 "1268" :stroke "black" :stroke-width "10"}]
   [:line {:x1 "10" :y1 "873" :x2 "620" :y2 "873" :stroke "black" :stroke-width "10"}]
   [:line {:x1 "300" :y1 "10" :x2 "300" :y2 "472" :stroke "black" :stroke-width "10"}]
   [:line {:x1 "300" :y1 "868" :x2 "300" :y2 "1340" :stroke "black" :stroke-width "10"}]
   [:line {:x1 "10" :y1 "675" :x2 "620" :y2 "675" :stroke "black" :stroke-width "10"}]])

(rum/defc field-comp
  < rum/reactive
  []
  (let [chs (rum/react current-hits)]
    ;; court measurements: width 610cm (+ 45 cm outside on both sides), height 1340 cm (+30 cm outside), scaled to half
    [:svg {:id "field" :width "350px" :height "700px" :style {:border "1px solid black" :padding "40px"} :viewBox "0 0 630 1350"
           :on-click #(track-hits %)}
     (field)
     (mapv draw-circle chs)
     (draw-strokes chs)]))

(rum/defc game-data < rum/reactive
  []
  (let [game-points (rum/react hits)
        current-points (rum/react current-hits)]
    [:div
     [:div "Current points: " (str current-points)]
     [:div "Game points: " (str game-points)]]))

(defn end-sequence []
  (swap! hits conj @current-hits)
  (reset! current-hits []))

(rum/defc end-button < (keyboard-mixin KeyCodes/ENTER #(end-sequence))
  []
  [:button {:on-click #(end-sequence)}
   "End sequence"])

(defn undo []
  (when (seq @current-hits)
    (swap! current-hits pop)))

(rum/defc undo-button < (keyboard-mixin KeyCodes/ESC #(undo))
  []
  [:button {:on-click #(undo)}
   "Undo"])

(defn update-last [key value]
  (let [latest (last @current-hits)]
    (when latest
      (swap! current-hits
             #(conj (pop %)
                    (assoc latest
                           key value))))))

(rum/defc shot-button < { :key-fn (fn [_ val] (name val))}
  [type val]
  [:button {:on-click #(update-last type val)}
   (clojure.string/capitalize (name val))])

(rum/defc shot-buttons < (keyboard-mixin KeyCodes/Q #(update-last :type :forehand))
                         (keyboard-mixin KeyCodes/W #(update-last :type :backhand))
                         (keyboard-mixin KeyCodes/E #(update-last :type :round))
                         (keyboard-mixin KeyCodes/A #(update-last :shot :clear))
                         (keyboard-mixin KeyCodes/S #(update-last :shot :smash))
                         (keyboard-mixin KeyCodes/D #(update-last :shot :drop))
                         (keyboard-mixin KeyCodes/Z #(update-last :fault :net))
  []
  [:div
   [:div
    (mapv #(shot-button :type %) [:forehand :backhand :round])]
   [:div
    (mapv #(shot-button :shot %) [:clear :smash :drop])]
   [:div
    (mapv #(shot-button :fault %) [:net])]])

(rum/defc player-court-selection
  []
  [:div
   [:div "Player court selection:"]
   [:label {:for :upper} "Upper court"]
   [:input {:type :radio
            :id :upper
            :name :player-side
            :defaultValue :upper
            :defaultChecked true
            :on-change #(reset! player-court :upper)}]
   [:label {:for :lower} "Lower court"]
   [:input {:type :radio
            :id :lower
            :name :player-side
            :defaultValue :lower
            :defaultChecked false
            :on-change #(reset! player-court :lower)}]])

(rum/defc root []
  [:div
   (field-comp)
   [:br]
   (end-button)
   (undo-button)
   (shot-buttons)
   (player-court-selection)
   (game-data)])

(rum/mount (root)
           (. js/document (getElementById "app")))
