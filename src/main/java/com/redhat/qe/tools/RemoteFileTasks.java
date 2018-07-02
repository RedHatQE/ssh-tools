package com.redhat.qe.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.redhat.qe.Assert;
import com.redhat.qe.jul.TestRecords;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;

public class RemoteFileTasks {
	protected static Logger log = Logger.getLogger(RemoteFileTasks.class.getName());

	/**
	 * Use echo to create a file with the given contents.  Then use chmod to give permissions to the file.
	 * @param runner
	 * @param filePath - absolute path to the file create
	 * @param contents - contents of the file
	 * @param perms - optional chmod options to apply to the filePath (e.g. "a+x")
	 * @return - exit code
	 * @author jsefler
	 */
	public static int createFile(SSHCommandRunner runner, String filePath, String contents, String perms) {
		//int exitCode = runCommandAndWait(runner, "echo -n -e '"+contents+"' > "+filePath, TestRecords.action());
		int exitCode = runCommandAndWait(runner, "echo -n -e '"+contents+"' | sudo tee -a "+filePath, TestRecords.action());
		if (exitCode==0 && perms!=null) exitCode = runCommandAndWait(runner, "sudo chmod "+perms+" "+filePath, TestRecords.action());
		return exitCode;
	}

	public static void createFile(SSHCommandRunner runner, String filePath, String contents) throws IOException, InterruptedException  {
		createFile(runner, filePath, contents, "0755");
	}

	/**
	 * Copy file(s) onto a remote machine 
	 * @param conn - A connection object already created to connect to ssh server
	 * @param destDir -  path where the file(s) should go on the remote machine (must be dir)
	 * @param source - one or more paths to the file(s) you want to copy to the remote dir
	 * @throws IOException
	 * @author jweiss, jstavel
	 */
	public static void putFiles(SSHCommandRunner runner, String destDir, String... sources ) throws IOException  {
    SSHClient client = runner.getConnection();
		SCPFileTransfer xfer = client.newSCPFileTransfer();
		for (String source: sources) {
			log.log(Level.INFO, "Copying " + source + " to " + destDir + " on " + client.getRemoteHostname(), TestRecords.Style.Action);
			xfer.upload(source,destDir);
		};
	}
	
	/**
	 * @param conn - A connection object already created to connect to ssh server
	 * @param source - path to the file you want to copy
	 * @param dest - full path to the destination where you want the file to go 
	 * 	(if path ends in trailing slash, it's assumed to be a dir, and the source filename is used) 
	 * @param mask - permissions on file, eg, "0755"
	 * @throws IOException
	 * @author jweiss, jstavel
	 */
	public static void putFile(SSHCommandRunner runner, String source, String dest, String mask) throws IOException  {
		assert(Pattern.matches("^[0-7]{3,4}", mask));
    SSHClient client = runner.getConnection();
		log.log(Level.INFO, "Copying local file " + source + " to " + dest + " on " + client.getRemoteHostname() + " with mask " + mask, TestRecords.Style.Action);
		client.newSCPFileTransfer().upload(source, dest);
    runner.runCommandAndWait("(test -d '" + dest + "')" + " && (echo 'is directory!')");
    final String isDirectorySTDOUT = runner.getStdout().trim();
    if (isDirectorySTDOUT.equals("is directory!")) {
      log.log(Level.INFO, dest + " - is a directory.....");
      	Path destFileName = Paths.get(dest, new File(source).getName());
        final String command = "chmod " + mask + " '" + destFileName + "'";
        log.log(Level.INFO, command);
      	runner.runCommand(command);
    } else {
      log.log(Level.INFO, dest + " - is not a directory.");
      final String command = "chmod " + mask + " '" + dest + "'";
      runner.runCommand(command);
    }
	}
	
	/**
	 * Copy file(s) from a remote machine 
	 * @param conn - can be retrieved from your SSHCommandRunner instance
	 * @param localTargetDirectory - Local directory to put the downloaded file(s).
	 * @param remoteFiles - Path and name(s) of the remote file(s)
	 * @throws IOException
	 * @author jsefler, jstavel
	 */
	public static void getFiles(SSHCommandRunner runner, String localTargetDirectory, String... remoteFiles ) throws IOException {
    SSHClient client = runner.getConnection();
		SCPFileTransfer xfer = client.newSCPFileTransfer();
		for (String remoteFile: remoteFiles) {
			log.log(Level.INFO, "Copying remote file "+remoteFile+" on "+client.getRemoteHostname()+" to local directory "+localTargetDirectory+".", TestRecords.Style.Action);
			xfer.download(remoteFile, localTargetDirectory);
		}
	}
	public static void getFile(SSHCommandRunner runner, String localTargetDirectory, String remoteFile ) throws IOException {
		getFiles(runner,localTargetDirectory,remoteFile);
	}
	
	/**
	 * Use sed to search and replace content within a file.<br>
	 * sed -i 's/regexp/replacement/g' filePath
	 * @param runner
	 * @param filePath - absolute path to the file to be searched and replaced
	 * @param regexp - the regular expression used to match a pattern for replacement
	 * @param replacement - the replacement content
	 * <BR>Note: in case your regexp or replacement contains a / character in it, you will need to call .replaceAll("/", "\\/") as you pass them to this method.
	 * @return - exit code from sed
	 * 
	 */
	public static int searchReplaceFile (SSHCommandRunner runner, String filePath, String regexp, String replacement) {
		return runCommandAndWait(runner, "sudo sed -i 's/"+regexp+"/"+replacement+"/g' " + filePath, TestRecords.action());
	}
	
	/**
	 * Use grep to search for the existence of an extended regular expression within a file.<br>
	 * grep -E 'searchTerm' filePath
	 * @param runner
	 * @param filePath - absolute path to the file to be searched
	 * @param pattern - an  extended  regular  expression (man grep for help)
	 * @return - exit code from grep
	 */
	public static int grepFile (SSHCommandRunner runner, String filePath, String pattern) {
		return runCommandAndWait(runner, "sudo grep -E '" + pattern + "' " + filePath, TestRecords.info());
	}
	
	/**
	 * Use sed to delete lines from a file.<br>
	 * sed -i '/containingText/d' filePath
	 * @param runner
	 * @param filePath - absolute path to the file from which lines will be deleted
	 * @param containingText - delete lines containing a match to this text
	 * @return - exit code from sed
	 * @author jsefler
	 */
	public static int deleteLines (SSHCommandRunner runner, String filePath, String containingText) {
		return runCommandAndWait(runner, "sudo sed -i '/"+containingText+"/d' " + filePath, TestRecords.action());
	}
	
	/**
	 * Use echo to append a marker string to the end of an existing file (e.g. a log file).<br>
	 * @param runner
	 * @param filePath - path to an existing file on the runner machine.  If this file does not exist, then 1 is returned and no mark is made.
	 * @param marker - this string will be appended to the file and act as a marker.  DO NOT USE CHARACTERS THAT WILL BE INTERPRETED BY BASH IN THIS STRING (e.g. PARENTHESIS).
	 * @return exit code from echo
	 * @author jsefler
	 */
	public static int markFile (SSHCommandRunner runner, String filePath, String marker) {
		if (!testExists(runner, filePath)) return 1;	// avoid inadvertently creating a file that does not exist
		//return runCommandAndWait(runner, "sudo echo '"+marker+"' >> "+filePath, TestRecords.action());
		return runCommandAndWait(runner, "echo '"+marker+"' | sudo tee -a "+filePath, TestRecords.action());
	}
	
	/**
	 * Return the tail of a file up to, but not including, the marker string (that was previously appended to the file).<br>
	 * This method is intended for use with the static markFile() method.  The idea is that a log file is first marked by your test,
	 * then the program that you are testing will append information to the log file, then by calling this method your test can get the tail
	 * of the log from the point after the mark to the end of file. 
	 * @param runner
	 * @param filePath - path to the file on the runner machine
	 * @param marker - string that was previously appended to the file by calling the markFile(...) method
	 * @param grepPattern - if not null, the tail of the file past the marker string will be greped for this pattern and the matching lines are returned.
	 * @return stdout
	 * @author jmolet
	 * @author jsefler
	 */
	public static String getTailFromMarkedFile (SSHCommandRunner runner, String filePath, String marker, String grepPattern) {		
		if (!testExists(runner, filePath)) Assert.fail("Cannot getTailFromMarkedFile '"+filePath+"' that does not exist.");

		/* INEFFICIENT ALGORITHM DATED 5/16/2011
		String grepCommand = "";
		if (grepPattern!=null) grepCommand =  " | grep -E '"+grepPattern+"'";
		SSHCommandResult result = runCommandAndAssert(runner,"(TAIL=''; IFS=$'\\n'; for line in $(tac "+filePath+"); do if [[ $line = '"+marker+"' ]]; then break; fi; if [[ $TAIL = '' ]]; then TAIL=$line; else TAIL=$line'\\n'${TAIL}; fi; done; echo -e $TAIL)"+grepCommand,0,1); // when grepCommand!=null, exitCode=0 means a match was found exitCode=1 means no match was found 
		return result.getStdout();
		*/
		
		/* INEFFICIENT ALGORITHM DATED 6/29/2011
		if (grepPattern!=null) {
			return runCommandAndAssert(runner,"(TAIL=''; IFS=$'\\n'; for line in $(egrep '"+grepPattern+"|"+marker+"' "+filePath+" | tac); do if [[ $line = '"+marker+"' ]]; then break; fi; if [[ $TAIL = '' ]]; then TAIL=$line; else TAIL=$line'\\n'${TAIL}; fi; done; echo -e $TAIL)",0).getStdout();
		} else {
			return runCommandAndAssert(runner,"(TAIL=''; IFS=$'\\n'; for line in $(tac "+filePath+"); do if [[ $line = '"+marker+"' ]]; then break; fi; if [[ $TAIL = '' ]]; then TAIL=$line; else TAIL=$line'\\n'${TAIL}; fi; done; echo -e $TAIL)",0).getStdout();
		}
		*/
		
		/* EFFICIENT ALGORITHM DATED 1/28/2013 */
		if (grepPattern!=null) {
			return runCommandAndAssert(runner,"sudo awk '/"+marker+"/,0' "+filePath+" | grep -v --text '"+marker+"' | grep --text '"+grepPattern+"'",0,1).getStdout();
		} else {
			return runCommandAndAssert(runner,"sudo awk '/"+marker+"/,0' "+filePath+" | grep -v --text '"+marker+"'",0,1).getStdout();
		}
	}
	
	/**
	 * Test for the existence of a file.<br>
	 * test -e filePath && echo 1 || echo 0
	 * @param runner
	 * @param filePath - absolute path to the file to test for existence
	 * @return 1 (file exists), 0 (file does not exist), -1 (could not determine existence)
	 * @author jsefler
	 */
	@Deprecated
	public static int testFileExists (SSHCommandRunner runner, String filePath) {
		runCommandAndWait(runner, "sudo test -e "+filePath+" && echo 1 || echo 0", TestRecords.info());
		if (runner.getStdout().trim().equals("1")) return 1;
		if (runner.getStdout().trim().equals("0")) return 0;
		return -1;
		
		// Note: Another more informative way to implement this is using: stat filePath
	}
	
	/**
	 * Test for the existence of a file.<br>
	 * @param runner
	 * @param filePath - absolute path to the file to test for existence
	 * @return boolean
	 * @author jsefler
	 */
	public static boolean testExists (SSHCommandRunner runner, String filePath) {
		SSHCommandResult result = runner.runCommandAndWait("sudo test -e "+filePath);
		return (result.exitCode==0)?true:false;
		
		// Note: Another more informative way to implement this is using: stat filePath
	}
	
	public static int runCommandAndWait(SSHCommandRunner runner, String command, LogRecord logRecord){
		return runner.runCommandAndWait(command,logRecord).getExitCode();
		//return runner.runCommandAndWait(command,Long.valueOf(30000),logRecord);	// timeout after 30 sec
	}
	
	public static int runAugeasCommand(SSHCommandRunner runner, String command, LogRecord logRecord){
		return runCommandAndWait(runner, String.format("echo -e \"%s\nsave\" | augtool", command), logRecord);
	}

	public static int updateAugeasConfig(SSHCommandRunner runner, String augeusPath, String newValue){
		if (newValue == null)
			return runAugeasCommand(runner, String.format("rm %s", augeusPath), TestRecords.action());
		else
			return runAugeasCommand(runner, String.format("set %s '%s'", augeusPath, newValue), TestRecords.action());
	}
	
	
	public static SSHCommandResult runCommandAndAssert(SSHCommandRunner sshCommandRunner, String command, Integer exitCode, List<String> stdoutRegexs, List<String> stderrRegexs) {
		List<Integer> exitCodes = null;
		if (exitCode != null) {
			exitCodes = new ArrayList<Integer>(); exitCodes.add(exitCode);
		}
		return runCommandAndAssert(sshCommandRunner, command, exitCodes, stdoutRegexs, stderrRegexs);
	}
	
	/**
	 * Use the sshCommandRunner to execute the given command and verify that stdout and stderr
	 * contains one or more matches to an expected regex expression. <br>
	 * Note: Assert.assertContainsMatch(...) will be used verify the output.  That means the regex
	 * does not have to match the entire output to be a successful match.
	 * @param sshCommandRunner
	 * @param command - command to execute with options
	 * @param validExitCodes - a list of expected exit codes from the command (usually 0 on success, non-0 on failure).  If the actual exit code matches 
	 * any code in this list, the assert passes.
	 * @param stdoutRegexs - List of expected regex expressions.  Each regex is asserted  to match a substring from the command's stdout
	 * @param stderrRegexs - List of expected regex expressions.  Each regex is asserted  to match a substring from the command's stderr
	 * @author jsefler
	 */
	public static SSHCommandResult runCommandAndAssert(SSHCommandRunner sshCommandRunner, String command, List<Integer> validExitCodes, List<String> stdoutRegexs, List<String> stderrRegexs) {

		SSHCommandResult sshCommandResult = sshCommandRunner.runCommandAndWait(command);
		if (validExitCodes!=null) {
			Assert.assertContains(validExitCodes, sshCommandResult.getExitCode());
		}
		if (stdoutRegexs!=null) {
			for (String regex : stdoutRegexs) {
				Assert.assertContainsMatch(sshCommandResult.getStdout(),regex,"Stdout",String.format("Stdout from command '%s' contains matches to regex '%s',",command,regex));
			}
		}
		if (stderrRegexs!=null) {
			for (String regex : stderrRegexs) {
				Assert.assertContainsMatch(sshCommandResult.getStderr(),regex,"Stderr",String.format("Stderr from command '%s' contains matches to regex '%s',",command,regex));
			}
		}
		
		return sshCommandResult;
	}
	public static SSHCommandResult runCommandAndAssert(SSHCommandRunner sshCommandRunner, String command, Integer exitCode, String stdoutRegex, String stderrRegex) {
		List<String> stdoutRegexs = null;
		if (stdoutRegex!=null) {
			stdoutRegexs = new ArrayList<String>();	stdoutRegexs.add(stdoutRegex);
		}
		List<String> stderrRegexs = null;
		if (stderrRegex!=null) {
			stderrRegexs = new ArrayList<String>();	stderrRegexs.add(stderrRegex);
		}
		return runCommandAndAssert(sshCommandRunner,command,exitCode,stdoutRegexs,stderrRegexs);
	}

	public static SSHCommandResult runCommandAndAssert(SSHCommandRunner sshCommandRunner, String command, Integer... exitCodes) {
		return runCommandAndAssert(sshCommandRunner,command,Arrays.asList(exitCodes),new ArrayList<String>(),new ArrayList<String>());
	}

	/**
	 * Occasionally, you may need to run commands, expecting a nonzero exit code.
	 * 
	 * If you run into this situation, this is your method.
	 * @param sshCommandRunner your preferred sshCommandRunner
	 * @param command - command to execute with options
	 * @author ssalevan
	 */
	public static SSHCommandResult runCommandExpectingNonzeroExit(SSHCommandRunner sshCommandRunner, String command){
		return runCommandExpectingNonzeroExit(sshCommandRunner, command, null);
	}
	
	/**
	 * Occasionally, you may need to run commands, expecting a nonzero exit code.
	 * 
	 * If you run into this situation, this is your method.
	 * @param sshCommandRunner your preferred sshCommandRunner
	 * @param command - command to execute with options
	 * @param timeout - in milliseconds
	 * @author whayutin
	 */
	public static SSHCommandResult runCommandExpectingNonzeroExit(SSHCommandRunner sshCommandRunner, String command, Long timeout){
// THIS WILL ALWAYS RETURN FALSE SINCE THE COMPARISON IS BETWEEN TWO DIFFERENT INSTANTIATED OBJECTS EVEN THOUGH THEIR VALUES MAY BE EQUAL - jsefler 7/9/2010 		
//		Assert.assertNotSame(sshCommandRunner.runCommandAndWait(command,timeout),
//				0,
//				"Command returns nonzero error code: "+command);
		SSHCommandResult sshCommandResult = sshCommandRunner.runCommandAndWait(command,timeout);
		Assert.assertTrue(!sshCommandResult.getExitCode().equals(0),"Command '"+command+"' returns nonzero error code: "+sshCommandResult.getExitCode());
		return sshCommandResult;
	}
	
	

	public static SSHCommandResult runCommandExpectingNoTracebacks(SSHCommandRunner sshCommandRunner, String command){
		return runCommandExpectingNoTracebacks( sshCommandRunner, command,  null);
	}
	
	public static SSHCommandResult runCommandExpectingNoTracebacks(SSHCommandRunner sshCommandRunner, String command, Long timeout){
		SSHCommandResult sshCommandResult = sshCommandRunner.runCommandAndWait(command,timeout);
		Assert.assertFalse(sshCommandResult.getStdout().toLowerCase().contains("traceback"),
				"Traceback string not in stdout");
		Assert.assertFalse(sshCommandResult.getStderr().toLowerCase().contains("traceback"),
				"Traceback string not in stderr");
		return sshCommandResult;
	}
}
