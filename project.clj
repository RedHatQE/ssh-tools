(defproject com.github.redhatqe/ssh-tools "2.1.2-SNAPSHOT"
  :description "A wrapper for sshj and some CLI tools"
  :url "https://github.com/RedHatQE/ssh-tools"
  :license {:name "GPL-3.0"
            :comment "GNU General Public License v3.0"
            :url "https://choosealicense.com/licenses/gpl-3.0"
            :year 2024
            :key "gpl-3.0"}
  :java-source-paths ["src/main/java"] ;lein2
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :javac-options {:debug "on"}
  :dependencies [[com.redhat.qe/jul.test.records "1.0.1"],
                 [com.redhat.qe/assertions "1.0.2"]
                 [com.hierynomus/sshj "0.38.0"]
                 [org.bouncycastle/bcprov-jdk15on "1.70"]
                 [com.jcraft/jzlib "1.1.3"]
                 [org.clojure/clojure "1.8.0"]
                 [net.i2p.crypto/eddsa "0.3.0"]]
  :repositories [["jenkins-ci" "https://repo.jenkins-ci.org/public"]
                 ["releases" {:url "https://repo.clojars.org" :creds :gpg}]]
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :profiles {:dev {:plugins [[com.jakemccrary/lein-test-refresh "0.25.0"]]
                   :dependencies [[junit/junit "4.13.2"]
                                  [yogthos/config "1.1.9"]]
                   :java-source-paths ["src/main/java" "src/test/java"]
                   :resource-paths ["src/test/resources"]}}
)
