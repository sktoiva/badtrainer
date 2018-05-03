(ns badtrainer.main.server
  (:require [com.stuartsierra.component :as component]
            [badtrainer.server.system :as system]))

(def system nil)

(defn- set-system [system]
  (alter-var-root #'system (constantly system)))

(defn- shutdown []
  (component/stop-system system))

(defn -main []
  (let [badtrainer (system/badtrainer-system {:port (-> "PORT"
                                                        System/getenv
                                                        Integer/parseInt)})]
    (set-system (component/start-system badtrainer))
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. shutdown))))

