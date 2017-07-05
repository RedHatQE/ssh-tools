(defproject com.redhat.qe/ssh-tools "1.0.3-SNAPSHOT"
  :description "Extensions of trilead ssh and some CLI tools"
  :java-source-path "src" ;lein1
  :java-source-paths ["src"] ;lein2
  :javac-options {:debug "on"}
  :dependencies [;[com.trilead/trilead-ssh2 "build213-svnkit-1.3-patch"]
                 [org.jenkins-ci/trilead-ssh2 "build-217-jenkins-11"]
                 [com.redhat.qe/jul.test.records "1.0.1"],
                 [com.redhat.qe/assertions "1.0.2"]]
  :repositories [["jenkins-ci" "https://repo.jenkins-ci.org/public"]])
