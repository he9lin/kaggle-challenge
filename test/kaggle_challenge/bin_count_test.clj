(ns kaggle-challenge.bin-count-test
  (:use [midje sweet cascalog]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.test :refer :all]
            [kaggle-challenge.bin-count :refer :all]))

(deftest bin-count-test
  (let [in (hfs-delimited "data/sample_transactions2.csv"
                          :delimiter ","
                          :skip-header? true)]
    (fact (run-bin-count in) =>
      (produces
        [["1823880850" 2 0 1 1 0 0]
         ["469510920"  2 3 0 2 0 0]]))))
