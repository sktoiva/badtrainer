(ns badtrainer.ui.stylesheet
  (:require [garden.def :refer [defstyles]]))


(defstyles style
  [:circle {:pointer-events :none}]
  [:path {:pointer-events :none}]
  [:.field-section {:pointer-events :all}
   [:&:hover {:fill "#ccc"}]])
