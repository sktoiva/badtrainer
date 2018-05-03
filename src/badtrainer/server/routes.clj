(ns badtrainer.server.routes
  (:require [io.pedestal.http :as http]
            [badtrainer.server.views :as views]))

(defn app [_]
  {:status 200
   :body (views/index)
   :headers {"Content-Type" "text/html"}})

(def routes
  #{["/" :get app :route-name :index]})



