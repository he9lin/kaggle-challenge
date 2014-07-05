(ns kaggle-challenge.bin-counter
  (:use [cascalog.api]
        [cascalog.checkpoint]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.string :as s]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [cascalog.logic [ops :as c] [vars :as v]]))

(defn parse-date [date-str]
  (let [date-parser (f/formatter
                      (t/default-time-zone) "YYYY-MM-dd" "YYYY/MM/dd")]
    (f/parse date-parser date-str)))

(defn days-between [date1 date2]
  (let [convert-arg (fn [d] (if (string? d) (parse-date d) d))
        date1 (convert-arg date1)
        date2 (convert-arg date2)]
    (t/in-days (t/interval date1 date2))))

(def transactions
  [["c1" "2012-05-01"]
   ["c1" "2012-05-02"]
   ["c1" "2012-05-03"]
   ["c1" "2012-05-05"]
   ["c1" "2012-05-08"]
   ["c1" "2012-05-11"]
   ["c1" "2012-05-21"]
   ["c1" "2012-05-29"]
   ["c2" "2012-05-01"]
   ["c2" "2012-05-03"]
   ["c2" "2012-05-08"]
   ["c2" "2012-05-11"]
   ["c2" "2012-05-21"]])

(def bins [1 3 7 9 30])
(def bin-vars (vec (map #(str "?a" %) bins)))

(defn num-bucket [num]
  (find-first (partial <= num) bins))

(defn index-of-bin [bin]
  (.indexOf bins bin))

(defaggregatefn agg-bins
  ([] [0 0 0 0 0 nil])
  ([total value]
   (let [prev-value (last total)
         bin        (num-bucket (if (nil? prev-value) 0
                                  (days-between prev-value value)))
         index      (index-of-bin bin)
         new-count  (+ (total index) 1)]
     (assoc
       (assoc total 5 value) index new-count)))
  ([total] [total]))

(def vars (into bin-vars ["?s"]))

(defn bin-count [in]
  (<- [?cust ?a1 ?a3 ?a7 ?a9 ?a30 ?s]
    (in ?cust ?value)
    (agg-bins :< ?value :>> vars)))

(defn clean [in]
  (<- [?cust ?aa1 ?a3 ?a7 ?a9 ?a30]
      (in ?cust ?a1 ?a3 ?a7 ?a9 ?a30 _)
      (- ?a1 1 :> ?aa1)))

(defn run []
  (?- (stdout)
      (clean (bin-count transactions))))

(run)
