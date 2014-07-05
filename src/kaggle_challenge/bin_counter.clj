(ns kaggle-challenge.bin-counter
  (:use [cascalog.api]
        [cascalog.checkpoint]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.string :as s]
            [cascalog.logic [ops :as c] [vars :as v]]))

(def custs
  [["c1" 1]
   ["c1" 2]
   ["c1" 3]
   ["c1" 5]
   ["c1" 8]
   ["c1" 11]
   ["c1" 21]
   ["c1" 50]
   ["c2" 1]
   ["c2" 3]
   ["c2" 8]
   ["c2" 11]
   ["c2" 21]])

(def bins [1 3 7 9 30])

(defn num-bucket [num]
  (find-first (partial <= num) bins))

;; (defn bin-count [in]
;;   (<- [?cust-id ?range ?count]
;;     (in ?cust-id ?num)
;;     (num-bucket ?num :> ?range)
;;     (c/count ?count)))
;;
;; (defn index-of-bin [bin]
;;   (.indexOf bins bin))
;;
;; (defaggregatefn agg-bins-fn
;;   ([] [0 0 0 0 0])
;;   ([total val1 val2] (let [index (index-of-bin val1)]
;;                        (assoc total index val2)))
;;   ([total] [total]))
;;
;; (def bin-vars (vec (map #(str "?a" %) bins)))
;;
;; (defn agg-bins [in]
;;   (<- [?cust ?a1 ?a3 ?a7 ?a9 ?a30]
;;     (in ?cust ?bin ?count)
;;     (agg-bins-fn :< ?bin ?count :>> bin-vars)))
;;
;; (defn run []
;;   (?- (stdout)
;;       (agg-bins (bin-count custs))))
;;
;; (run)

(defaggregatefn super-bins-fn
  ([] [0 0 0 0 0 0])
  ([total value]
   (let [prev-value (last total)
         bin        (num-bucket (- value prev-value))
         index      (index-of-bin bin)
         new-count  (+ (total index) 1)]
     (assoc
       (assoc total 5 value) index new-count)))
  ([total] [total]))

(def super-bins-vars (into bin-vars ["?s"]))

(defn super-bins [in]
  (<- [?cust ?a1 ?a3 ?a7 ?a9 ?a30 ?s]
    (in ?cust ?value)
    (super-bins-fn :< ?value :>> super-bins-vars)))

(defn -clean [in]
  (<- [?cust ?aa1 ?a3 ?a7 ?a9 ?a30]
      (in ?cust ?a1 ?a3 ?a7 ?a9 ?a30 _)
      (- ?a1 1 :> ?aa1)))

(defn super-run []
  (?- (stdout)
      (-clean (super-bins custs))))

(super-run)
