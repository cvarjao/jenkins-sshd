SSHD server for Jenkins (BETA)
====================

This project creates a SSHD server implementing only the necessary commands used by Jenkins [SSH Slaves plugin](https://wiki.jenkins-ci.org/display/JENKINS/SSH+Slaves+plugin)

Using an thin SSH server allows:
* slave.jar provision/update
* encrypted channel  (instead of the non-encrypted JNLP)
* ability to restart slave from master with a new (clean) JVM
