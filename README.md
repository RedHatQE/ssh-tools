# SSH tools for automated testing

Contains some extensions to a SSH library that make running remote command easier (see **SSHCommandRunner**). 
Allows running commands on remote host and returning separate stdout, stderr.  Also has some functions to copy and move files
from one host to another (see **RemoteFileTasks**).

## Prerequisities of testing
   - ssh into a test server to accept a server key
   ```shell
   ssh root@my-server.domain.com
   ```
   - copy a test key to the test server
   ```shell
   ssh-copy-id -i src/test/resources/test_rsa.pub root@my-server.domain.com
   ```
   - try to use the key with ssh
   ```shell
   ssh -i src/test/resources/test_rsa root@my-server.domain.com
   ```
   
## Testing

Most tests are written in `clojure`. You can see them in `src/test/clojure`. Or see `src/test/java` for junit tests.

Configuration of tests is placed in `src/test/resources`.

The vulnerable part of config file is read from a system environment.

```shell
export SERVER_HOSTNAME="some-rserver.domain.com"
export SERVER_PASSWORD="user-password"
export PRIVATE_KEY_PASSWORD="key-password"
lein test-refresh
```
