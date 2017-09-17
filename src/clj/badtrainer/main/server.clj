(ns badtrainer.main.server
  (:require [badtrainer.core :as core]))

(def server (atom nil))

(defn- shutdown []
  (core/stop @server))

(defn -main []
  (reset! server (core/start))
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. shutdown)))
