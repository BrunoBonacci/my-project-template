(ns perf.benchmarks
  (:require [{{group-id}}.{{name}} :refer :all]))


;; write your test benchmarks here:
(defn test-sleep
  []
  (Thread/sleep 1))
