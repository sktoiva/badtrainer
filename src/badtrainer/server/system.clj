(ns badtrainer.server.system
  (:require [com.stuartsierra.component :as component]
            [badtrainer.server.component.server :as server]))

(defn badtrainer-system [conf]
  (component/system-map
   :web-server (server/new-web-server conf)))



