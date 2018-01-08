import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.redhat.qe.tools.SSHCommandRunner;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;

public class sshCommandRunner {
	protected String hostname;
	protected String password;
	protected String user;
	
	@Before
	public void loadEnvironment() {
		hostname = System.getProperty("server.hostname");
		user = System.getProperty("server.user");
		password = System.getProperty("server.password");
		assertNotNull(hostname);
		assert(!hostname.isEmpty());
		assertNotNull(password);
		assert(!password.isEmpty());
	}
	
	@Test
	public void testRunCommandString() throws IOException, InterruptedException {
		SSHCommandRunner cmd = new SSHCommandRunner(hostname, user, password, "hostname");
		cmd.runCommand("hostname");
		String stdout = cmd.getStdout();
		assert(hostname.contains(stdout.trim()));
		String stderr = cmd.getStderr();
		assert(stderr.trim().isEmpty());
		Integer exitcode = cmd.getExitCode();
		assertNull(exitcode);
		cmd.waitForWithTimeout(1000l);
		assert(cmd.getExitCode() == 0);
	}

	@Test
	public void testSSHClientExec() throws IOException {
		SSHClient ssh = new SSHClient();
		try {
			ssh.loadKnownHosts();
			ssh.connect("jstavel-rhel7-server.usersys.redhat.com");
			ssh.authPassword("root", "redhat");
			Session session = ssh.startSession();
			Command cmd = session.exec("hostname");
			cmd.join();
			assert(cmd.getExitStatus() == 0);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ssh.close();
		}
	}
	
	@Test
	public void testWaitFor() throws IOException {
		SSHCommandRunner cmd = new SSHCommandRunner(hostname, user, password, "hostname");
		cmd.runCommand("hostname");
		cmd.waitForWithTimeout(1000l);
		String stderr = cmd.getStderr();
		assert(stderr.trim().isEmpty());
		String stdout = cmd.getStdout();
		assert(hostname.contains(stdout.trim()));
		Integer exitcode = cmd.getExitCode();
		assert(exitcode == 0);
	}
}
