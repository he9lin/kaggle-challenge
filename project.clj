(defproject kaggle-challenge "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [cascalog/cascalog-core "2.1.0"]
                 [cascalog/cascalog-checkpoint "2.1.0"]
                 [cascalog/cascalog-more-taps "2.1.0"]]
  :jvm-opts ["-Xms768m" "-Xmx768m"]
  :profiles {
             :dev  {:plugins [[lein-midje "3.1.1"]]
                    :dependencies [[org.apache.hadoop/hadoop-core "1.1.2"]
                                   [lein-midje "3.1.1"]
                                   [cascalog/midje-cascalog "2.1.0"]]}})
