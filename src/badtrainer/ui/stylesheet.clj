(ns badtrainer.ui.stylesheet
  (:require [garden.def :refer [defstyles]]))


(defstyles style
  [:.circle {:pointer-events :none}]
  [:.path {:pointer-events :none}]
  [:.field {:border "1px solid black"}]
  [:.field-section {:pointer-events :all}
   [:&:hover {:fill "#ccc"}]])
