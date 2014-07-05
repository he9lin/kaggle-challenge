(ns kaggle-challenge.play
  (:use [cascalog.api]
        [cascalog.checkpoint]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.string :as s]
            [cascalog.logic [ops :as c] [vars :as v]]))

(use 'cascalog.playground) (bootstrap)

;; This inserts combiners
(defn sum-count-1 []
  (?<- (stdout) [?count ?sum]
     (integer ?n) (c/sum ?n :> ?sum) (c/count ?count)))


;; this doesn't
(defaggregatefn product
  ([] 1)
  ([total val] (* total val))
  ([total] [total]))

(defn sum-count-2 []
  (?<- (stdout) [?count ?sum]
     (integer ?n) (product ?n :> ?sum) (c/count ?count)))

;; this will have combiners
(defparallelagg dosum :init-var #'identity :combine-var #'+)
(defparallelagg doprod :init-var #'identity :combine-var #'*)

(defn sum-count-3 []
  (?<- (stdout) [?count ?sum]
     (integer ?n) (dosum ?n :> ?sum) (c/count ?count)))

(defn sum-count-4 []
  (?<- (stdout) [?count ?sum]
     (integer ?n) (doprod ?n :> ?sum) (c/count ?count)))

;; (sum-count-4)

;; Implicit equality constraints
(defn selfie [] (?<- (stdout) [?n] (integer ?n) (* ?n ?n :> ?n)))
;; => 1

(defn pair-same [] (?<- (stdout) [?n] (num-pair ?n ?n)))
;; => all pairs the has same two elements

(defn sec-twice-first [] (?<- (stdout) [?n1 ?n2]
                              (num-pair ?n1 ?n2) (* 2 ?n1 :> ?n2)))

;; (sec-twice-first)













