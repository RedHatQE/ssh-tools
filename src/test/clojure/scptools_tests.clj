(ns scptools-tests
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.string :as str])
  (:import [com.redhat.qe.tools SSHCommandRunner]
           [com.redhat.qe.tools SCPTools]
           [net.schmizz.sshj.xfer.scp.SCPFileTransfer]
           [java.util UUID]))

(def hostname "jstavel-rhel7-server.usersys.redhat.com")
(def user "root")
(def password "redhat")
(def ssh-key-pem-file-path "src/test/resources/test_rsa")

(deftest sendFile-test
  (let [ssh-key-pem-file (io/file ssh-key-pem-file-path)
        scptools (new SCPTools hostname user ssh-key-pem-file password)
        runner (new SSHCommandRunner hostname user password "hostname")]
    (let [uuid (-> (UUID/randomUUID) .toString)
          tmp-dir (io/file "/tmp" uuid)
          file01 (-> "src/test/resources/file01.txt" io/file)]
      (is (.exists file01))
      (.runCommand runner (str "mkdir " "'" tmp-dir "'"))
      (.sendFile scptools (.getAbsolutePath file01) (.toString tmp-dir))
      (let [[_ stdout stderr] ((juxt #(.runCommand %1 (str "(test -d " "'" tmp-dir "') && (echo 'is directory!')"))
                                     #(.getStdout %1)
                                     #(.getStderr %1)) runner)]
        (is (= "is directory!" (.trim stdout)))
        (.close scptools)
        )
      )
    )
  )

(deftest getFile-test
  (let [ssh-key-pem-file (io/file ssh-key-pem-file-path)
        uuid (-> (UUID/randomUUID) .toString)
        tmp-dir (io/file "/tmp" uuid)
        path-of-tmp-dir (.toString tmp-dir)
        runner (new SSHCommandRunner hostname user password "hostname")
        file01 (-> "src/test/resources/file01.txt" io/file)
        scptools (new SCPTools hostname user ssh-key-pem-file password)]
    (.runCommand runner (str "mkdir " tmp-dir))
    (.sendFile scptools (.getAbsolutePath file01) (.toString tmp-dir))
    (shell/with-sh-env {:LC_ALL "en_US.UTF-8"}
      (-> (shell/sh "mkdir" path-of-tmp-dir)
          :exit
          (= 0)
          is)
      (->> (shell/sh "ls" "-al" path-of-tmp-dir)
           :out
           s/split-lines
           first
           (= "total 0")
           is))
    (let [remote-file01 (-> (io/file tmp-dir "file01.txt"))]
      (.getFile scptools (.toString remote-file01) path-of-tmp-dir)
      (shell/with-sh-env {:LC_ALL "en_US.UTF-8"}
        (->> (shell/sh "ls" "-al" path-of-tmp-dir)
             :out
             s/split-lines
             (filter (fn [line] (.contains line "file01.txt")))
             count
             (= 1)
             is)
        (let [content-of-copied-file01 (->> (shell/sh "cat" (.toString (io/file tmp-dir "file01.txt"))) :out)
              content-of-original-file01 (->> (shell/sh "cat" (.getAbsolutePath file01)) :out)]
          (is (= content-of-original-file01 content-of-copied-file01)))))))

