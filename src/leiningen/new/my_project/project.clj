(defn ver [] "{{version}}")
(defn ts  [] (System/currentTimeMillis))
(defn jdk [] (clojure.string/replace (str (System/getProperty "java.vm.vendor") "-" (System/getProperty "java.vm.version")) #" " "_"))

(defproject {{group-id}}/{{name}} #=(ver)
  :description "FIXME: write description"

  :url "https://github.com/{{github-user}}/{{name}}"

  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :scm {:name "git" :url "https://github.com/{{github-user}}/{{name}}.git"}

  :dependencies [[org.clojure/clojure "{{clojure-version}}"]]

  :global-vars {*warn-on-reflection* true}

  :jvm-opts ["-server" "-Djdk.attach.allowAttachSelf"]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[midje "{{midje-version}}"]
                                  [org.clojure/test.check "{{test-check-version}}"]
                                  [criterium "{{criterium-version}}"]
                                  [org.slf4j/slf4j-log4j12 "{{slf4j-log4j}}"]
                                  [com.clojure-goes-fast/clj-async-profiler "{{clj-prof-ver}}"]
                                  [jmh-clojure "{{jmh-version}}"]]
                   :resource-paths ["dev-resources"]
                   :plugins      [[lein-midje "{{lein-midje-version}}"]
                                  [lein-jmh "{{lein-jmh-version}}"]]}}

  :aliases
  {"perf-quick"
   ["with-profile" "dev" "jmh"
    #=(pr-str {:file "./dev/perf/benchmarks.edn"
               :status true :pprint true :format :table
               :fork 1 :measurement 5
               :output #=(clojure.string/join "-" ["./reservoir" #=(ver) #=(jdk) #=(ts) "results.edn"])})]

   "perf"
   ["with-profile" "dev" "jmh"
    #=(pr-str {:file "./dev/perf/benchmarks.edn"
               :status true :pprint true :format :table
               :output #=(clojure.string/join "-" ["./reservoir" #=(ver) #=(jdk) #=(ts) "results.edn"])})]
   }
  )
