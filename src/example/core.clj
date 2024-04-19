(ns example.core
  (:require [com.stuartsierra.component :as component]
            [example.database :as db]
            [example.application :as app]
            [example.webserver :as server]))

(defn create-system []
  (component/system-map
   :application (app/create {:config {:logging true}})
   :database (db/create {:dbtype "sqlite", :dbname "test.db"} db/create+insert)
   :webserver (server/create server/handler 3333)))

(comment
  (def system (create-system))
  (alter-var-root (var system) component/start)
  (alter-var-root (var system) component/stop)

  :rcf)