(ns kaggle-challenge.core
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

(defn group_by_id [in]
  (<- [?id ?items]
      (in ?id ?item)
      (doaccum ?item :> ?items)))

(defn remove_id [input]
  (<- [?items] (input _ ?items)))

(defn skip_items [in skips]
  (<- [?customer-id-date ?item]
      (in ?customer-id-date ?item)
      (skips ?item :> true)))

(defn items_to_skip [skips build-item-id-fn]
  (<- [?item]
      (skips _ ?category _ ?company _ ?brand)
      (build-item-id-fn ?category ?company ?brand :> ?item)))

(defn items_assocs
  ([in build-item-id-fn]
   (remove_id
     (group_by_id
        (build-customer-id-date-key in build-item-id-fn))))
  ([in skips build-item-id-fn]
   (let [skips (items_to_skip skips build-item-id-fn)]
     (remove_id
       (group_by_id
         (skip_items
            (build-customer-id-date-key in build-item-id-fn) skips))))))

(defn products_assocs [& params]
  (apply items_assocs (into (vec params) [#'build-id])))

(defn build-brand-id [cate company brand] brand)
(defn brands_assocs [& params]
  (apply items_assocs (into (vec params) [#'build-brand-id])))

(defn -main
  ([in out]
    (let [in (hfs-delimited in :delimiter "," :skip-header? true)]
      (?- (hfs-delimited out)
          (brands_assocs in))))
  ([in skips out]
    (let [in (hfs-delimited in :delimiter "," :skip-header? true)
          skips (hfs-delimited skips :delimiter "," :skip-header? true)]
      (?- (hfs-delimited out)
          (brands_assocs in skips)))))
