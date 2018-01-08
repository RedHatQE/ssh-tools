(defproject com.redhat.qe/ssh-tools "2.0.1-SNAPSHOT"
  :description "A wrapper for sshj and some CLI tools"
  :java-source-paths ["src/main/java"] ;lein2
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :javac-options {:debug "on"}
  :dependencies [[com.redhat.qe/jul.test.records "1.0.1"],
                 [com.redhat.qe/assertions "1.0.2"]
                 [com.hierynomus/sshj "0.23.0"]
                 [org.bouncycastle/bcprov-jdk15on "1.57"]
                 [org.bouncycastle/bcpkix-jdk15on "1.57"]
                 [com.jcraft/jzlib "1.1.3"]
                 [org.clojure/clojure "1.8.0"]
                 [net.i2p.crypto/eddsa "0.2.0"]]
  :repositories [["jenkins-ci" "https://repo.jenkins-ci.org/public"]]
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :profiles {:dev {:plugins [[com.jakemccrary/lein-test-refresh "0.22.0"]]
                   :dependencies [[spyscope "0.1.5"]
                                  [junit/junit "4.12"]
                                  [yogthos/config "0.9"]]
                   :java-source-paths ["src/main/java" "src/test/java"]
                   :resource-paths ["src/test/resources"]
                   :injections [(require 'spyscope.core)]}}
)
