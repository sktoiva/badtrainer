(ns badtrainer.main.server
  (:require [badtrainer.server.core :as core]))

(def server (atom nil))

(defn- shutdown []
  (core/stop @server))

(defn -main []
  (reset! server (core/start (-> "PORT"
                                 System/getenv
                                 Integer/parseInt)))
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. shutdown)))

