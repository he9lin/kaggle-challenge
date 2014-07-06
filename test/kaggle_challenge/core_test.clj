(ns kaggle-challenge.core-test
  (:use [midje sweet cascalog]
        [cascalog.more-taps :only (hfs-delimited)])
  (:require [clojure.test :refer :all]
            [kaggle-challenge.core :refer :all]))
