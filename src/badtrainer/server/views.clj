(ns badtrainer.server.views)

(defn index []
  (slurp "resources/public/index.html"))
