(ns example.webserver
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]))

(defn handler [_]
  {:status 200
   :body "OK"})

(defrecord WebServer [handler-fn port ;; jetty に渡す
                      application ;; 依存
                      http-server]
  component/Lifecycle
  (start [this]
    (if http-server
      this
      (assoc this :http-server (jetty/run-jetty handler-fn
                                                {:port port :join? false}))))
  (stop [this]
    (if http-server
      (do
        (.stop http-server)
        (assoc this :http-server nil))
      this)))

(defn create [handler-fn port]
  (component/using
   (map->WebServer {:handler-fn handler-fn :port port})
   [:application]))

(comment
  (def server (create handler 3333))
  (alter-var-root (var server) component/start)
  (alter-var-root (var server) component/stop)
  :rcf)