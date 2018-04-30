(ns badtrainer.ui.util.keyboard
  (:require [goog.events :as events]
            [goog.ui.KeyboardShortcutHandler :as KeyboardShortcutHandler]
            [goog.ui.KeyboardShortcutHandler.EventType :as EventType])
  (:import [goog.ui KeyboardShortcutHandler]))

(defn install-listener!
  "Installs a listener for keyboard events.
   If target is not given it's attached to window."
  [key f target]
  (let [handler (new KeyboardShortcutHandler target)]
    (.registerShortcut handler (str key true) key)
    (events/listen
     handler
     EventType/SHORTCUT_TRIGGERED
     (fn [e]
       (f e)))
    (fn []
      (.unregisterShortcut handler key))))

(defn keyboard-mixin
  "Triggers f-keydown when key is pressed and f-keyup when key is lifted  while the component is mounted.
   if target is a function it will be called AFTER the component
   mounted with state and should return a dom node that is the target
   of the listener.  If no target is given it is defaulted to
   js/window (global handler) Ex:
     (keyboard-mixin \"esc\" #(browse-to :home/home))"
  ([key f] (keyboard-mixin key f js/window))
  ([key f target]
   (let [target-fn (if (fn? target) target (fn [_] target))]
     {:did-mount
      (fn [state]
        (assoc state ::keyboard-listener
                     (install-listener! key f (target-fn state))))
      :will-unmount
      (fn [state]
        (::keyboard-listener state)
        state)})))
