(defproject {{group-id}}/{{name}} "{{version}}"
  :description "FIXME: write description"

  :url "https://github.com/{{github-user}}/{{name}}"

  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :scm {:name "git" :url "https://github.com/{{github-user}}/{{name}}.git"}

  :dependencies [[org.clojure/clojure "{{clojure-version}}"]]

  :global-vars {*warn-on-reflection* true}

  :jvm-opts ["-server"]

  :profiles {:dev {:dependencies [[midje "{{midje-version}}"]
                                  [org.clojure/test.check "{{test-check-version}}"]
                                  [criterium "{{criterium-version}}"]
                                  [org.slf4j/slf4j-log4j12 "{{slf4j-log4j}}"]]
                   :resource-paths ["dev-resources"]
                   :plugins      [[lein-midje "{{lein-midje-version}}"]]}}
  )
