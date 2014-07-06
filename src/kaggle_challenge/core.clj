(ns kaggle-challenge.core
  (:use [cascalog.api]
        [cascalog.checkpoint]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [kaggle-challenge.assocs :refer :all]
            [clojure.string :as s]
            [cascalog.logic [ops :as c] [vars :as v]])
  (:gen-class))

;; products-assocs
;; brands-assocs
;; run-bin-count

(defn -main
  ([in out]
    (let [in (hfs-delimited in :delimiter "," :skip-header? true)]
      (?- (hfs-delimited out)
          (brands-assocs in))))
  ([in skips out]
    (let [in (hfs-delimited in :delimiter "," :skip-header? true)
          skips (hfs-delimited skips :delimiter "," :skip-header? true)]
      (?- (hfs-delimited out)
          (brands-assocs in skips)))))
