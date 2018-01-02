(ns ssh-command-runner-tests
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [clojure.java.io :as io])
  (:import [com.redhat.qe.tools SSHCommandRunner]
           [net.schmizz.sshj SSHClient]
           [net.schmizz.sshj.userauth.keyprovider KeyProvider]
           [net.schmizz.sshj.connection.channel.direct Session]
           )
  )

(def hostname "jstavel-rhel7-server.usersys.redhat.com")
(def user "root")
(def password "redhat")
(def private-key-path "src/test/resources/test_rsa")
(def private-key-password "redhat")

;; (deftest ssh-client-test
;;   (let [ssh (new SSHClient)]
;;     (doto ssh
;;       .loadKnownHosts
;;       (.connect hostname)
;;       (.authPassword user password))
;;     (let [session (.startSession ssh)]
;;       (let [cmd (.exec session "hostname")]
;;         (.join cmd)
;;         (is (= 0 (.getExitStatus cmd)))))
;;     (.close ssh)))

;; (deftest ssh-client-rsa-key-test
;;   (let [ssh (new SSHClient)]
;;     (doto ssh
;;       .loadKnownHosts
;;       (.connect hostname))
;;     (let [keypar (.loadKeys ssh
;;                             (-> private-key-path
;;                                 io/file
;;                                 (.getAbsolutePath))
;;                             (char-array private-key-password))]
;;       (.authPublickey ssh user [keypar])
;;       (let [session (.startSession ssh)]
;;         (let [cmd (.exec session "hostname")]
;;           (.join cmd)
;;           (is (= 0 (.getExitStatus cmd))))))
;;     (.close ssh)))

;; (deftest ssh-command-runner-test
;;   (let [cmd (new SSHCommandRunner hostname user password "hostname")]
;;     (.runCommand cmd "hostname")
;;     (let [stdout (.. cmd getStdout trim)
;;           stderr (. cmd getStderr)]
;;       (is (s/starts-with? hostname stdout))
;;       (is (s/blank? stderr)))
;;     (let [exitcode (. cmd getExitCode)]
;;       (is (nil? exitcode))
;;       (. cmd waitForWithTimeout 1000)
;;       (is (= 0 (. cmd getExitCode))))))

;; (deftest ssh-command-runner-rsa-key-test
;;   (let [key-file (io/file private-key-path)
;;         cmd (new SSHCommandRunner hostname user key-file password "hostname")]
;;     (.runCommand cmd "hostname")
;;     (let [stdout (.. cmd getStdout trim)
;;           stderr (. cmd getStderr)]
;;       (is (s/starts-with? hostname stdout))
;;       (is (s/blank? stderr)))
;;     (let [exitcode (. cmd getExitCode)]
;;       (is (nil? exitcode))
;;       (. cmd waitForWithTimeout 1000)
;;       (is (= 0 (. cmd getExitCode))))))
