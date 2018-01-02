(ns remote-files-tasks-tests
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.java.shell :as shell]
            [clojure.string :as str])
  (:import [com.redhat.qe.tools SSHCommandRunner]
           [com.redhat.qe.tools RemoteFileTasks]
           [net.schmizz.sshj.xfer.scp.SCPFileTransfer]
           [java.util UUID]))

(def hostname "jstavel-rhel7-server.usersys.redhat.com")
(def user "root")
(def password "redhat")

;; (deftest scpfiletransfer-test
;;   (let [uuid (-> (UUID/randomUUID) .toString)
;;         tmp-dir (io/file "/tmp" uuid)
;;         runner (new SSHCommandRunner hostname user password "hostname")
;;         file01 (-> "src/test/resources/file01.txt" io/file)]
;;     (is (.exists file01))
;;     (.runCommand runner (str "mkdir " tmp-dir))
;;     (let [connection (.getConnection runner)]
;;       (-> connection
;;           .newSCPFileTransfer
;;           (.upload (.getAbsolutePath file01) (.toString tmp-dir))))
;;     (let [[_ stdout stderr] ((juxt #(.runCommand %1 (str "(test -d " "'" tmp-dir "') && (echo 'is directory!')"))
;;                                    #(.getStdout %1)
;;                                    #(.getStderr %1)) runner)]
;;       (is (= "is directory!" (.trim stdout))))))

;; (deftest put-file-test
;;   (let [uuid (-> (UUID/randomUUID) .toString)
;;         tmp-dir (io/file "/tmp" uuid)
;;         runner (new SSHCommandRunner hostname user password "hostname")
;;         file01 (-> "src/test/resources/file01.txt" io/file)]
;;     (is (.exists file01))
;;     (.runCommand runner (str "mkdir " tmp-dir))
;;     (RemoteFileTasks/putFile runner (-> file01 .getAbsolutePath) (.toString tmp-dir) "0666")
;;     (.runCommand runner (str "ls -al " tmp-dir))
;;     (let [lines (-> runner .getStdout s/split-lines)
;;           file-line (->> lines
;;                          (filter (fn [line] (.contains line "file01.txt")))
;;                          first)]
;;       (is (not (nil? file-line)))
;;       (let [rights (-> file-line
;;                        (s/split #" +")
;;                        first)]
;;         (is (s/includes? rights "-rw-rw-rw-"))))))

;; (deftest put-files-test
;;   (let [uuid (-> (UUID/randomUUID) .toString)
;;         tmp-dir (io/file "/tmp" uuid)
;;         runner (new SSHCommandRunner hostname user password "hostname")
;;         file01 (-> "src/test/resources/file01.txt" io/file)
;;         file02 (-> "src/test/resources/file02.txt" io/file)]
;;     (is (.exists file01))
;;     (is (.exists file02))
;;     (.runCommand runner (str "mkdir " tmp-dir))
;;     (RemoteFileTasks/putFiles runner (. tmp-dir toString) (into-array String [(. file01 getAbsolutePath) (. file02 getAbsolutePath)]))
;;     (.runCommand runner (str "ls -al " tmp-dir))
;;     (let [lines (-> runner .getStdout s/split-lines)
;;           file01-line (->> lines
;;                            (filter (fn [line] (.contains line "file01.txt")))
;;                            first)
;;           file02-line (->> lines
;;                            (filter (fn [line] (.contains line "file02.txt")))
;;                            first)]
;;       (is (not (nil? file01-line)))
;;       (is (not (nil? file02-line))))))


;; (deftest get-file-test
;;   (let [uuid (-> (UUID/randomUUID) .toString)
;;         tmp-dir (io/file "/tmp" uuid)
;;         path-of-tmp-dir (.toString tmp-dir)
;;         runner (new SSHCommandRunner hostname user password "hostname")
;;         file01 (-> "src/test/resources/file01.txt" io/file)
;;         file02 (-> "src/test/resources/file02.txt" io/file)]
;;     (.runCommand runner (str "mkdir " tmp-dir))
;;     (RemoteFileTasks/putFiles runner
;;                               path-of-tmp-dir
;;                               (into-array String [(. file01 getAbsolutePath) (. file02 getAbsolutePath)]))

;;     (shell/with-sh-env {:LC_ALL "en_US.UTF-8"}
;;       (-> (shell/sh "mkdir" path-of-tmp-dir)
;;           :exit
;;           (= 0)
;;           is)
;;       (->> (shell/sh "ls" "-al" path-of-tmp-dir)
;;            :out
;;            s/split-lines
;;            first
;;            (= "total 0")
;;            is))
;;     (let [remote-file01 (-> (io/file tmp-dir "file01.txt"))
;;           remote-file02 (-> (io/file tmp-dir "file02.txt"))]
;;       (RemoteFileTasks/getFile runner path-of-tmp-dir (.toString remote-file01))
;;       (shell/with-sh-env {:LC_ALL "en_US.UTF-8"}
;;         (->> (shell/sh "ls" "-al" path-of-tmp-dir)
;;              :out
;;              s/split-lines
;;              (filter (fn [line] (.contains line "file01.txt")))
;;              count
;;              (= 1)
;;              is)
;;         (let [content-of-copied-file01 (->> (shell/sh "cat" (.toString (io/file tmp-dir "file01.txt"))) :out)
;;               content-of-original-file01 (->> (shell/sh "cat" (.getAbsolutePath file01)) :out)]
;;           (is (= content-of-original-file01 content-of-copied-file01)))))))

;; (deftest get-files-test
;;   (let [uuid (-> (UUID/randomUUID) .toString)
;;         tmp-dir (io/file "/tmp" uuid)
;;         path-of-tmp-dir (.toString tmp-dir)
;;         runner (new SSHCommandRunner hostname user password "hostname")
;;         file01 (-> "src/test/resources/file01.txt" io/file)
;;         file02 (-> "src/test/resources/file02.txt" io/file)]
;;     (.runCommand runner (str "mkdir " tmp-dir))
;;     (-> (shell/sh "mkdir" path-of-tmp-dir) :exit (= 0) is)
;;     (RemoteFileTasks/putFiles runner
;;                               path-of-tmp-dir
;;                               (into-array String [(. file01 getAbsolutePath) (. file02 getAbsolutePath)]))
;;     (let [remote-file01 (-> (io/file tmp-dir "file01.txt"))
;;           remote-file02 (-> (io/file tmp-dir "file02.txt"))]
;;       (RemoteFileTasks/getFiles runner
;;                                 path-of-tmp-dir
;;                                 (into-array String [(.toString remote-file01) (.toString remote-file02)]))
;;       (shell/with-sh-env {:LC_ALL "en_US.UTF-8"}
;;         (->> (shell/sh "ls" path-of-tmp-dir)
;;              :out
;;              s/split-lines
;;              (into #{})
;;              (= #{"file01.txt" "file02.txt"})
;;              is)
;;         (let [content-of-copied-file01 (->> (shell/sh "cat" (.toString (io/file tmp-dir "file01.txt"))) :out)
;;               content-of-original-file01 (->> (shell/sh "cat" (.getAbsolutePath file01)) :out)]
;;           (is (= content-of-original-file01 content-of-copied-file01)))))))
