(ns badtrainer.ui.util.keyboard
  (:require [goog.events :as events]
            [goog.events.EventType :as EventType]))

(defn listen [key target event f]
  (events/listenOnce
   target
   event
   (fn [e]
     (when (= (.-keyCode e) key) (f e)))) )

(defn install-listener!
  "Installs a listener for keyboard events.
   If target is not given it's attached to window."
  ([key f-keydown f-keyup] (install-listener! key f-keydown f-keyup js/window))
  ([key f-keydown f-keyup target]
   (when f-keydown (listen key target EventType/KEYDOWN f-keydown))
   (when f-keyup (listen key target EventType/KEYUP f-keyup))
   ;; Always reinstall the listener
   (events/listen
    target
    EventType/KEYUP
    (fn [e]
      (when (= (.-keyCode e) key)
        (install-listener! key f-keydown f-keyup target))))))

(defn keyboard-mixin
  "Triggers f-keydown when key is pressed and f-keyup when key is lifted  while the component is mounted.
   if target is a function it will be called AFTER the component
   mounted with state and should return a dom node that is the target
   of the listener.  If no target is given it is defaulted to
   js/window (global handler) Ex:
     (keyboard-mixin \"esc\" #(browse-to :home/home))"
  ([key f-keydown] (keyboard-mixin key f-keydown nil js/window))
  ([key f-keydown f-keyup] (keyboard-mixin key f-keydown f-keyup js/window))
  ([key f-keydown f-keyup target]
   (let [target-fn (if (fn? target) target (fn [_] target))]
     {:did-mount
      (fn [state]
        (assoc state ::keyboard-listener
                     (install-listener! key f-keydown f-keyup (target-fn state))))
      :will-unmount
      (fn [state]
        (::keyboard-listener state)
        state)})))
