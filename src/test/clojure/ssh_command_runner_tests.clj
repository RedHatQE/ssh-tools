(ns ssh-command-runner-tests
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [config.core :refer [env]])
  (:import [com.redhat.qe.tools SSHCommandRunner]
           [net.schmizz.sshj SSHClient]
           [net.schmizz.sshj.userauth.keyprovider KeyProvider]
           [net.schmizz.sshj.connection.channel.direct Session]))

(def hostname (atom ""))
(def user (atom ""))
(def password (atom ""))
(def private-key-path (atom ""))
(def private-key-password (atom ""))

(defn load-config [f]
  (reset! hostname (:server-hostname env))
  (reset! user (:server-user env))
  (reset! password (:server-password env))
  (reset! private-key-path (:private-key-path env))
  (reset! private-key-password (:private-key-password env))
  (f))

(use-fixtures :once load-config)

(deftest ssh-client-test
  (let [ssh (new SSHClient)]
    (doto ssh
      .loadKnownHosts
      (.connect @hostname)
      (.authPassword @user @password))
    (let [session (.startSession ssh)]
      (let [cmd (.exec session "hostname")]
        (.join cmd)
        (is (= 0 (.getExitStatus cmd)))))
    (.close ssh)))

(deftest ssh-client-rsa-key-test
  (let [ssh (new SSHClient)]
    (doto ssh
      .loadKnownHosts
      (.connect @hostname))
    (let [keypar (.loadKeys ssh
                            (-> @private-key-path
                                io/file
                                (.getAbsolutePath))
                            (char-array @private-key-password))]
      (.authPublickey ssh @user [keypar])
      (let [session (.startSession ssh)]
        (let [cmd (.exec session "hostname")]
          (.join cmd)
          (is (= 0 (.getExitStatus cmd))))))
    (.close ssh)))

(deftest ssh-command-runner-test
  (let [cmd (new SSHCommandRunner @hostname @user @password "hostname")]
    (.runCommand cmd "hostname")
    (let [stdout (.. cmd getStdout trim)
          stderr (. cmd getStderr)]
      (is (s/starts-with? @hostname stdout))
      (is (s/blank? stderr)))
    (let [exitcode (. cmd getExitCode)]
      (is (nil? exitcode))
      (. cmd waitForWithTimeout 1000)
      (is (= 0 (. cmd getExitCode))))))

(deftest ssh-command-runner-rsa-key-test
  (let [key-file (io/file @private-key-path)
        cmd (new SSHCommandRunner @hostname @user key-file @password "hostname")]
    (.runCommand cmd "hostname")
    (let [stdout (.. cmd getStdout trim)
          stderr (. cmd getStderr)]
      (is (s/starts-with? @hostname stdout))
      (is (s/blank? stderr)))
    (let [exitcode (. cmd getExitCode)]
      (is (nil? exitcode))
      (. cmd waitForWithTimeout 1000)
      (is (= 0 (. cmd getExitCode))))))
