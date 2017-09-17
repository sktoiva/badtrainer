(ns badtrainer.core
  (:require [io.pedestal.http :as http]
            [badtrainer.views :as views]))

(defn respond-hello [request]
  {:status 200
   :body "Hello World!"})

(defn app [_]
  {:status 200
   :body (views/index)
   :headers {"Content-Type" "text/html"}})

(def routes
  #{["/greet" :get `respond-hello]
    ["/app" :get `app]})

(defn start []
  (-> {::http/routes routes
       ::http/join? false
       ::http/port 8891
       ::http/resource-path "public"
       ::http/secure-headers {:content-security-policy-settings {:object-src "none"}}
       ::http/type :jetty}
      http/create-server
      http/start))

(defn stop [server]
  (http/stop server))

(comment
  (def server (start))
  server
  (http/stop server)
  )
