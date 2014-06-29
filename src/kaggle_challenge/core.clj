(ns kaggle-challenge.core
  (:use [cascalog.api]
        [cascalog.checkpoint]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.string :as s]
            [cascalog.logic [ops :as c] [vars :as v]])
  (:gen-class))

(defbufferfn doaccum [tuples] [(s/join "," (map first tuples))])

(def build-id #(s/join "-" %&))

(defn products_by_customer_and_date [input]
  (let [trans (hfs-delimited input :delimiter "," :skip-header? true)]
    (<- [?customer-id-date ?product]
        (trans ?customer-id _ _ ?category ?company ?brand ?date _ _ _ _)
        (build-id ?customer-id ?date :> ?customer-id-date)
        (build-id ?category ?company ?brand :> ?product))))

(defn products_per_customer_and_date [input]
  (<- [?customer-id-date ?products]
      (input ?customer-id-date ?product)
      (doaccum ?product :> ?products)))

(defn products_assocs [input]
  (<- [?products]
      (input _ ?products)))

(defn -main [in out]
  (?- (hfs-delimited out)
      (products_assocs
        (products_per_customer_and_date
          (products_by_customer_and_date in)))))

;; (-main "data/sample_transaction" "data/sample_result")
