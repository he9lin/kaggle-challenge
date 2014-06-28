(ns kaggle-challenge.core-test
  (:require [clojure.test :refer :all]
            [kaggle-challenge.core :refer :all]))

(fact "Query should return tuples of items frequently appear together"
      (transactions_by_customer :path) => (produces [["richhickey" 2961]])
      (provided
        (complex-subquery :path) =>
          [
           ["9909-107143070-5072" "6901-103700030-16139"]
           ["6901-103700030-16139" "6901-103700030-16139" "2119-101200010-10522"]
           ["2210-103700030-5174" "2222-103700030-5122"]
           ["2119-101200010-10522" "2628-103700030-2248"]
           ["2628-103700030-2248"]
          ]))
