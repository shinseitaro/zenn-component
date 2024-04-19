(ns example.database
  (:require [next.jdbc :as jdbc]
            [com.stuartsierra.component :as component]))

(defrecord DataBase [db-spec datasource init-fn]
  component/Lifecycle
  (start [this]
    (if datasource
      this
      (let [datasource (jdbc/get-datasource db-spec)]
        (init-fn datasource)
        (assoc this :datasource datasource))))
  (stop [this]
    (assoc this :datasource nil)))

(defn create [db-spec init-fn]
  (map->DataBase {:db-spec db-spec :init-fn init-fn}))

(defn create+insert [ds]
  (try
    (jdbc/execute! ds
                   ["CREATE TABLE users (
                   id INTEGER PRIMARY KEY AUTOINCREMENT,
                   name TEXT,
                   email TEXT)"])
    (try
      (jdbc/execute! ds
                     ["INSERT INTO users (name, email) 
                        VALUES ('taro', 'taro@test.com')"])
      (catch Exception e
        (println "EXEPTION :" (ex-message e))
        (println "初期データ挿入に失敗しました")))

    (catch Exception e
      (println "EXEPTION :" (ex-message e))
      (println "Create Tableに失敗しました。データベースはすでに設定済みではないですか？"))))

(comment
  (def db (create {:dbtype "sqlite", :dbname "test.db"} create+insert))
  (alter-var-root (var db) component/start)
  (alter-var-root (var db) component/stop)

  :rcf)
