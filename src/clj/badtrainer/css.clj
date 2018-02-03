(ns badtrainer.css
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [:body {:background "#00F"}]

  [:.game {:width "100%"}]

  [:.section:hover {:background :black}]

  [:.field-container {:display :flex
                      :justify-content :center
                      :position "fixed"
                      :top 0
                      :left 0
                      :width "100%"
                      :height "100%"
                      :padding "40px 0"}]

  [:.field {:height "calc(100vh - 80px)"
            :margin "0 50px" }]

  [:.row {:display :flex
          :flex-direction :row
          :justify-content :center}]

  [:.field-sections {:width "100%"
                     :display :flex
                     :flex-direction :column}])
