(ns perf
  (:require [{{group-id}}.{{name}} :refer :all]
            [criterium.core :refer [bench quick-bench]]))


(comment

  ;; perf tests

  (bench (Thread/sleep 1000))

  )