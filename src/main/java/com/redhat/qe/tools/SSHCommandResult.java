package com.redhat.qe.tools;


public class SSHCommandResult {
	protected Integer exitCode = null;
	protected String stdout = null;
	protected String stderr = null;
	
	/**
	 * @param exitCode
	 * @param stdout
	 * @param stderr
	 */
	public SSHCommandResult(Integer exitCode, String stdout, String stderr) {
		super();
		this.exitCode = exitCode;
		this.stdout = stdout;
		this.stderr = stderr;
	}

	/**
	 * @return the exitCode
	 */
	public final Integer getExitCode() {
		return exitCode;
	}
	
	/**
	 * @return the stdout
	 */
	public final String getStdout() {
		return stdout;
	}

	/**
	 * @return the stderr
	 */
	public final String getStderr() {
		return stderr;
	}
	
	public String toString() {
		String string = "";
		if (exitCode != null)	string += String.format(" %s=%d", "exitCode",exitCode);
		if (stdout != null)		string += String.format(" %s='%s'", "stdout",stdout);
		if (stderr != null)		string += String.format(" %s='%s'", "stderr",stderr);
		
		return string.trim();
	}
}
