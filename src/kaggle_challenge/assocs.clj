(ns kaggle-challenge.assocs
  (:use [cascalog.api]
        [cascalog.checkpoint]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.string :as s]
            [cascalog.logic [ops :as c] [vars :as v]])
  (:gen-class))

(defbufferfn doaccum [tuples] [(s/join "," (map first tuples))])

(def build-id #(s/join "-" %&))

(defn build-customer-id-date-key [in build-item-id-fn]
  (<- [?customer-id-date ?item]
      (in ?customer-id _ _ ?category ?company ?brand ?date _ _ _ _)
      (build-id ?customer-id ?date :> ?customer-id-date)
      (build-item-id-fn ?category ?company ?brand :> ?item)))

(defn group-by-id [in]
  (<- [?id ?items]
      (in ?id ?item)
      (doaccum ?item :> ?items)))

(defn remove-id [input]
  (<- [?items] (input _ ?items)))

(defn skip-items [in skips]
  (<- [?customer-id-date ?item]
      (in ?customer-id-date ?item)
      (skips ?item :> true)))

(defn items_to_skip [skips build-item-id-fn]
  (<- [?item]
      (skips _ ?category _ ?company _ ?brand)
      (build-item-id-fn ?category ?company ?brand :> ?item)))

(defn items-assocs
  ([in build-item-id-fn]
   (remove-id
     (group-by-id
        (build-customer-id-date-key in build-item-id-fn))))
  ([in skips build-item-id-fn]
   (let [skips (items_to_skip skips build-item-id-fn)]
     (remove-id
       (group-by-id
         (skip-items
            (build-customer-id-date-key in build-item-id-fn) skips))))))

(defn build-brand-id [cate company brand] brand)

(defn products-assocs [& params]
  (apply items-assocs (into (vec params) [#'build-id])))

(defn brands-assocs [& params]
  (apply items-assocs (into (vec params) [#'build-brand-id])))
