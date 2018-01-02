import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.redhat.qe.tools.RemoteFileTasks;
import com.redhat.qe.tools.SSHCommandResult;
import com.redhat.qe.tools.SSHCommandRunner;

public class RemoteFileTasksTest {
	protected String hostname;
	protected String password;
	protected String user;
	protected final String fileContents="some content";
	protected String remoteFileName;

	@Before
	public void loadEnvironment() {
		hostname = System.getProperty("server.hostname");
		user = System.getProperty("server.user");
		password = System.getProperty("server.password");
		assertNotNull(hostname);
		assert(!hostname.isEmpty());
		assertNotNull(password);
		assert(!password.isEmpty());
		final String randomPrefix = UUID.randomUUID().toString();
		remoteFileName = "/tmp/" + randomPrefix + ".txt";
	}
	
/*	@Test
	public void testCreateFileSSHClientStringStringString() throws IOException {
		SSHCommandRunner runner = new SSHCommandRunner(hostname, user, password, "hostname");
		RemoteFileTasks.createFile(runner, remoteFileName,
				fileContents, "0666");
		SSHCommandResult result = runner.runCommandAndWait("cat " + this.remoteFileName);
		final String remoteContent = result.getStdout().trim();
		assert(remoteContent.equals(fileContents));
		SSHCommandResult result01 = runner.runCommandAndWait("stat -c '%a' " + this.remoteFileName);
		final String remoteMod = result01.getStdout().trim();
		assert("666".equals(remoteMod));
	}*/

	@Test
	public void testPutFiles() throws IOException {
		SSHCommandRunner runner = new SSHCommandRunner(hostname, user, password, "hostname");
		URL file01 = getClass().getClassLoader().getResource("file01.txt"); //.toString();
		URL file02 = getClass().getClassLoader().getResource("file02.txt"); //.toString();
		System.out.println(file01);
		System.out.println(file01.getPath());
		System.out.println(file02);
		//RemoteFileTasks.putFile(runner, source, dest, mask);
	}

}
