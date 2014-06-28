(ns kaggle-challenge.core
  (:use [cascalog.api]
        [cascalog.checkpoint]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.string :as s]
            [cascalog.logic [ops :as c] [vars :as v]]))

(defn products_per_customer_date [input]
  (<- [?product]
      ))


(defn trans []
  (let [data-in "data/sample_transaction"
        data-out "./output/total-sales-per-city/"]
    (?- (hfs-delimited data-out :sinkmode :replace :delimiter ",")
        (total-sales-per-city
          (hfs-delimited data-in :delimiter ",")))))
(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
