(ns badtrainer.ui.stylesheet
  (:require [garden.def :refer [defstyles]]))


(defstyles style
  [:.row {:display :flex :flex-direction :row :width "100%"}]
  [:.in {:width "40px" :height "40px" :border "1px solid black"}]
  [:.first {:width "40px" :height "40px" :background :blue :border "1px solid black"}]
  [:.second {:width "40px" :height "40px" :background :red :border "1px solid black"}]
  [:.out {:width "40px" :height "40px"}])
