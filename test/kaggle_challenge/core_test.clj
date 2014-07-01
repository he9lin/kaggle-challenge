(ns kaggle-challenge.core-test
  (:use [midje sweet cascalog]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.test :refer :all]
            [kaggle-challenge.core :refer :all]))

(deftest products-assocs-test
  (let [in (hfs-delimited "data/sample_transactions.csv"
                          :delimiter ","
                          :skip-header? true)]
    (fact (products_assocs in) =>
      (produces
        [
         ["2210-103700030-5174,2222-103700030-5122"]
         ["2119-101200010-10522,2628-103700030-2248"]
         ["2628-103700030-2248"]
         ["2210-103700030-5174"]
         ["9909-107143070-5072,6901-103700030-16139"]
         ["6901-103700030-16139,2119-101200010-10522,2210-103700030-5174"]]))))
