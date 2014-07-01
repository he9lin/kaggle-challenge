(ns kaggle-challenge.core
  (:use [cascalog.api]
        [cascalog.checkpoint]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.string :as s]
            [cascalog.logic [ops :as c] [vars :as v]])
  (:gen-class))

(defbufferfn doaccum [tuples] [(s/join "," (map first tuples))])

(def build-id #(s/join "-" %&))

(defn products_by_customer_and_date [in]
  (<- [?customer-id-date ?product]
      (in ?customer-id _ _ ?category ?company ?brand ?date _ _ _ _)
      (build-id ?customer-id ?date :> ?customer-id-date)
      (build-id ?category ?company ?brand :> ?product)))

(defn products_per_customer_and_date [in]
  (<- [?customer-id-date ?products]
      (in ?customer-id-date ?product)
      (doaccum ?product :> ?products)))

(defn select_products [input]
  (<- [?products]
      (input _ ?products)))

(defn skips_products [in skips]
  (<- [?customer-id-date ?product]
      (in ?customer-id-date ?product)
      (skips ?product :> true)))

(defn products_assocs
  ([in]
   (->> in
        products_by_customer_and_date
        products_per_customer_and_date
        select_products))
  ([in skips]
   (let [skips (<- [?product]
                   (skips _ ?category _ ?company _ ?brand)
                   (build-id ?category ?company ?brand :> ?product))]
     (select_products
       (products_per_customer_and_date
         (skips_products
            (products_by_customer_and_date in) skips))))))

(defn -main [in out]
  (let [in (hfs-delimited in :delimiter "," :skip-header? true)]
    (?- (hfs-delimited out)
        (products_assocs in))))
