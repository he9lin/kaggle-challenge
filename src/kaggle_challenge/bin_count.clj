(ns kaggle-challenge.bin-count
  (:use [cascalog.api]
        [cascalog.checkpoint]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.string :as s]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [cascalog.logic [ops :as c] [vars :as v]]))

(defn find-first
    "Returns the first item of coll for which (pred item) returns logical true.
    Consumes sequences up to the first match, will consume the entire sequence
    and return nil if no match is found."
    [pred coll]
    (first (filter pred coll)))

(defn parse-date [date-str]
  (let [date-parser (f/formatter
                      (t/default-time-zone) "YYYY-MM-dd" "YYYY/MM/dd")]
    (f/parse date-parser date-str)))

(defn days-between [date1 date2]
  (let [convert-arg (fn [d] (if (string? d) (parse-date d) d))
        date1 (convert-arg date1)
        date2 (convert-arg date2)]
    (t/in-days (t/interval date1 date2))))

(def bins [1 3 7 30 90 180])
(def bin-vars (v/gen-nullable-vars (count bins)))

(defn num-bucket [num bins]
  (find-first (partial <= num) bins))

(defn index-of-bin [bin]
  (.indexOf bins bin))

(defn days-between-dates [prev curr]
  (if (nil? prev)
    nil
    (try
      (days-between prev curr)
      (catch Exception e nil))))

(defn find-bin-for-value [v]
  (if (or (nil? v) (zero? v)) nil (num-bucket v bins)))

(def agg-bins-base
  (into (into [] (replicate (count bins) 0)) [nil]))

(defaggregatefn agg-bins
  ([] agg-bins-base)
  ([total value]
   (let [prev-value (last total)
         bin        (find-bin-for-value (days-between-dates prev-value value))
         index      (index-of-bin bin)]
     (assoc
       (if (== index -1) total (assoc total index (+ (total index) 1)))
       (count bins) value)))
  ([total] [total]))

(def vars (into bin-vars ["?s"]))

(defn select-customer-and-date [in]
  (<- [?cust ?date]
    (in ?cust _ _ _ _ _ ?date _ _ _ _)))

(defn count-bins [in]
  (let [out-vars (into ["?cust"] vars)]
    (<- out-vars
      (in ?cust ?date)
      (agg-bins :< ?date :>> vars))))

(defn clean [in]
  (let [out-vars (into ["?cust"] bin-vars)]
    (<- out-vars
      (in :>> (into out-vars ["_"])))))

(defn run-bin-count [in]
  (->> in
       select-customer-and-date
       count-bins
       clean))

(def transactions (hfs-delimited "data/sample_transactions2.csv"
                                 :delimiter ","
                                 :skip-header? true))

(?- (stdout) (run-bin-count transactions))
