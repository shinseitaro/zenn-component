(ns example.application
  (:require [com.stuartsierra.component :as component]))

(defrecord Application [config ;; app の何らかの設定
                        database;; 依存するDB
                        status] ;; app の起動状態
  component/Lifecycle
  (start [this] (assoc this :status ::running))
  (stop [this] (assoc this :status ::stopped)))

(defn create [config]
  (component/using (map->Application {:config config}) ;; 第一引数にコンストラクタ関数
                   [:database])) ;; 第二期引数に依存する


(comment
  (def app (create {:logging true}))
  (alter-var-root (var app) component/start)
  (alter-var-root (var app) component/stop)
  :rcf)