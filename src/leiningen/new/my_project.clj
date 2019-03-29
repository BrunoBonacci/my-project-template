(ns leiningen.new.my-project
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]
            [clj-http.client :as http]
            [clojure.string :as str]))


(def ^:const CLOJARS_URL "https://clojars.org/api/artifacts/%s")
(def ^:const MAVEN_URL   "https://search.maven.org/solrsearch/select?q=g:\"%s\" AND a:\"%s\"&rows=1&wt=json&core=gav")

(def ^:const CLOJURE-PKG      "org.clojure/clojure")
(def ^:const TEST-CHECK-PKG   "org.clojure/test.check")
(def ^:const MIDJE-PKG        "midje")
(def ^:const LEIN-MIDJE-PKG   "lein-midje")
(def ^:const CRITERIUM-PKG    "criterium")
(def ^:const LEIN-BINPLUS-PKG "lein-binplus")
(def ^:const SLF4J-LOG4J-PKG  "org.slf4j/slf4j-log4j12")

(def render (renderer "my-project"))


(defn sort-by-semantic-version [versions]
  (->> versions
     (map (partial re-find #"(\d+)\.(\d+)\.(\d+)(?:\.(\d+))?(?:-(SNAPSHOT)|(.*))?"))
     (filter identity)
     (map (fn [[v a b c d sn alpha-beta]]
            (let [->int (fnil read-string "-1")]
              [(->int a) (->int b) (->int c) (->int d) (not= sn "SNAPSHOT") alpha-beta v])))
     sort
     (map last)))


(defn package-versions-clojars
  [package]
  (main/info "Searching for latest" package "version on Clojars.org ...")
  (->> (:body (http/get (format CLOJARS_URL package) {:accept :edn :as :clojure}))
     :recent_versions
     (map :version)))


(defn package-versions-maven
  [package]
  (main/info "Searching for latest" package "version on Maven Central ...")
  (let [[gid aid] (str/split package #"/")]
    (->> (:body (http/get (format MAVEN_URL gid aid) {:accept :json :as :json}))
       :response
       :docs
       (map :v))))



(defn package-versions
  [package]
  (if (str/includes? package "org.clojure/")
    (package-versions-maven package) ;; clojure artifacts are not up-to-date in Clojars
    (try
      (package-versions-clojars package)
      (catch Exception x
        (package-versions-maven package)))))



(defn latest-version
  [versions snapshot?]
  (->> versions
       sort-by-semantic-version
       (filter #(if snapshot? true (not (re-find #"SNAPSHOT" %))))
       last))


(defn latest-version-of [package snapshot?]
  (let [versions (package-versions package)
        latest   (latest-version versions snapshot?)]
    (main/info "Latest" package "version:" latest)
    latest))



(defn my-project
  "A leinengen template to create personal OSS projects."
  [name & options]
  (let [parsed (main/parse-options options)]
    (if (not (:error parsed))
      (let [data {:name           name
                  :sanitized      (name-to-path name)
                  :version        (or (:version parsed) "0.1.0-SNAPSHOT")
                  :group-id       "com.brunobonacci"
                  :sanitized-group (name-to-path "com.brunobonacci")
                  :github-user    "BrunoBonacci"
                  :author         "Bruno Bonacci"
                  :bootstrap-version   (:bootstrap-version parsed)
                  :clojure-version     (latest-version-of CLOJURE-PKG false)
                  :midje-version       (latest-version-of MIDJE-PKG false)
                  :test-check-version  (latest-version-of TEST-CHECK-PKG false)
                  :criterium-version   (latest-version-of CRITERIUM-PKG false)
                  :lein-midje-version  (latest-version-of LEIN-MIDJE-PKG false)
                  :slf4j-log4j         (latest-version-of SLF4J-LOG4J-PKG false)
                  :year                (format "%tY" (java.util.Date.))}]
        (main/info "Generating fresh project.")
        (->files data
                 ["project.clj" (render "project.clj" data)]
                 [".midje.clj" (render "midje.clj" data)]
                 ["src/{{sanitized-group}}/{{sanitized}}.clj" (render "core.clj" data)]
                 ["test/{{sanitized-group}}/{{sanitized}}_test.clj" (render "core_test.clj" data)]
                 ["README.md" (render "README.md" data)]
                 ["LICENSE" (render "LICENSE")]
                 ["doc/intro.md" (render "intro.md")]
                 [".gitignore" (render "gitignore")]
                 [".hgignore" (render "hgignore")]
                 ["dev/user.clj" (render "user.clj" data)]
                 ["dev/perf.clj" (render "perf.clj" data)]
                 ["dev-resources/log4j.properties" (render "log4j.properties" data)]
                 "resources")
        (main/info "All done.\n")
        (main/info "To build and test:\n\t$ lein do clean, midje"))
      (main/info (:error parsed)))))
