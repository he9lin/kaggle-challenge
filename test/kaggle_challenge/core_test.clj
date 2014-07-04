(ns kaggle-challenge.core-test
  (:use [midje sweet cascalog]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.test :refer :all]
            [kaggle-challenge.core :refer :all]))


(deftest brands-assocs-test
  (let [in (hfs-delimited "data/sample_transactions.csv"
                          :delimiter ","
                          :skip-header? true)]
    (fact (brands_assocs in) =>
      (produces
        [["5174,5122"]
         ["10522,2248"]
         ["2248"]
         ["5174"]
         ["5072,16139"]
         ["16139,10522,5174"]]))))

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

(deftest products-assocs-filtered-test
  (let [in (hfs-delimited "data/sample_transactions.csv"
                          :delimiter ","
                          :skip-header? true)
        skips (hfs-delimited "data/offers.csv"
                             :delimiter ","
                             :skip-header? true)]
    (fact (products_assocs in skips) =>
      (produces
        [
         ["2210-103700030-5174,2222-103700030-5122"]
         ["2119-101200010-10522"]
         ["2210-103700030-5174"]
         ["6901-103700030-16139,9909-107143070-5072"]
         ["2119-101200010-10522,2210-103700030-5174,6901-103700030-16139"]]))))
