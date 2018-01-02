package com.redhat.qe.tools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;

public class SCPTools {
	protected String userName;
	protected File sshPemFile;
	protected String password;
	protected String server;
	protected static Logger log = Logger.getLogger(SCPTools.class.getName());
	protected SSHClient connection = null;
	protected SCPFileTransfer client = null;
	
	public SCPTools(String server,
			String user,
			File sshPemFile,
			String password){
		this.userName = user;
		this.sshPemFile = sshPemFile;
		this.password = password;
		this.server = server;
	}
	
	public SCPTools(String server,
			String user,
			String sshPemFileLoc,
			String password){
		this.userName = user;
		this.sshPemFile = new File(sshPemFileLoc);
		this.password = password;
		this.server = server;
	}
	
	public boolean sendFile(String source, String dest){
		log.info("SCP: Copying "+source+" to "+this.server+":"+dest);

		try {
			init();
			client.upload(source, dest);
		} catch (IOException e) {
			log.log(Level.INFO, "SCP: File transfer failed:", e);
			return false;
		} 
		log.info("SCP: Transfer succeeded");
		
		
		
		return true;		
	}
	
	/*public void sendStream(OutputStream os, String dest) throws IOException{
		Connection newConn = new Connection(server);
		log.info("SFTP: Copying stream to "+this.server+":"+dest);
		newConn.connect();
		newConn.authenticateWithPublicKey(userName, sshPemFile, password);
		SFTPv3Client sftp = new SFTPv3Client(newConn);
		sftp.createFile(dest);
		
		log.info("SFTP: Transfer succeeded");
	}*/
	
	public boolean getFile(String remoteFile, String target){
		log.info("SCP: Copying "+server+":"+remoteFile+" to "+target);

		try {
			init();
			client.download(remoteFile, target);
		
		} catch (IOException e) {
			log.log(Level.INFO, "SCP: File transfer failed:", e);
			return false;
		}
		log.info("SCP: Transfer succeeded");
		
		return true;
	}

	
	public void close() {
		try {
			connection.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void init() throws IOException{
		if (connection == null) {
			connection = connect_server();
			client = connection.newSCPFileTransfer();
		}
	}
	
	private SSHClient connect_server() throws IOException{
		SSHClient ssh = new SSHClient();
		try {
      ssh.loadKnownHosts();
			ssh.connect(server);
			KeyProvider keyProvider = ssh.loadKeys(sshPemFile.toString(), password);
			ssh.authPublickey(userName, keyProvider);
			if(!ssh.isAuthenticated()) {
        log.log(Level.INFO, "SCP: auth public key failed, trying auth password.");
				ssh.authPassword(userName, password);
			}
		} catch (IOException e) {
			log.log(Level.INFO, "SCP: Connection failed:", e);			
		}
		return ssh;
	}
	
	public static void main(String... args) {
		SCPTools copier = new SCPTools("f14-1.usersys.redhat.com", "root", new File(""), "dog8code");
		copier.sendFile("/tmp/blah1", "/tmp/");
		copier.sendFile("/tmp/blah2", "/tmp");
		copier.sendFile("/tmp/blah3", "/tmp");
		copier.sendFile("/tmp/blah4", "/tmp");
		copier.sendFile("/tmp/blah5", "/tmp");
		copier.sendFile("/tmp/blah6", "/tmp");
		copier.sendFile("/tmp/blah7", "/tmp");
		copier.sendFile("/tmp/blah8", "/tmp");
		copier.sendFile("/tmp/blah9", "/tmp");
		copier.sendFile("/tmp/blah1", "/tmp/");
		copier.sendFile("/tmp/blah2", "/tmp");
		copier.sendFile("/tmp/blah3", "/tmp");
		copier.sendFile("/tmp/blah4", "/tmp");
		copier.sendFile("/tmp/blah5", "/tmp");
		copier.sendFile("/tmp/blah6", "/tmp");
		copier.sendFile("/tmp/blah7", "/tmp");
		copier.sendFile("/tmp/blah8", "/tmp");
		copier.sendFile("/tmp/blah9", "/tmp");
	
	}

}
