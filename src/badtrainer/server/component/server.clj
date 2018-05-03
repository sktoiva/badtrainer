(ns badtrainer.server.component.server
  (:require [com.stuartsierra.component :as component]
            [badtrainer.server.routes :as routes]
            [io.pedestal.http :as http]))

(defrecord WebServer [routes server service-map]
  component/Lifecycle
  (start [component]
    (if server
      component
      (let [server (-> service-map
                       http/create-server
                       http/start)]
        (assoc component :server server))))

  (stop [component]
    (if-not server
      component
      (do
        (http/stop server)
        (assoc component :server nil)))))

(defn new-web-server [{:keys [port]}]
  (map->WebServer
   {:service-map {::http/join? false
                  ::http/routes routes/routes
                  ::http/port port
                  ::http/resource-path "public"
                  ::http/secure-headers {:content-security-policy-settings {:object-src "none"}}
                  ::http/type :jetty}}))
