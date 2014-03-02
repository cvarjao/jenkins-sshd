package com.cvarjao.jenkins.sshd;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.sftp.SftpSubsystem;

import com.cvarjao.jenkins.sshd.authetication.PublickeyAuthenticatorImpl;
import com.cvarjao.jenkins.sshd.factory.JenkinsCommandFactory;
import com.cvarjao.jenkins.sshd.factory.JenkinsFileSystemFactory;
import com.cvarjao.jenkins.sshd.factory.JenkinsSessionFactory;

/***
 * 
 * @author Clécio Varjão
 * @see <p>
 *      http://caffeineiscodefuel.blogspot.ca/2013/04/apache-mina-sshd-publickeyauthenticator.html
 *      </p>
 *      <p>
 *      http://mina.apache.org/sshd-project/embedding_ssh.html
 *      </p>
 *      http://vfs-utils.sourceforge.net/shell/sshd.html
 */
public class Sshd {
	private static final String WELCOME = "Welcome to my server";
	private int port;
	private SshServer sshd;

	public void start() throws IOException {
		port = 22;

		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(port);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		sshd.setPublickeyAuthenticator(new PublickeyAuthenticatorImpl(new File("authorized_keys")));
		sshd.getProperties().put(SshServer.WELCOME_BANNER, WELCOME);
		sshd.getProperties().put(SshServer.AUTH_METHODS, "publickey");
		sshd.setSessionFactory(new JenkinsSessionFactory());
		sshd.setSubsystemFactories(Arrays.<NamedFactory<Command>>asList(new SftpSubsystem.Factory()));
		sshd.setFileSystemFactory(new JenkinsFileSystemFactory());
	    sshd.setCommandFactory(new JenkinsCommandFactory());
		
		sshd.start();
		while(true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		try {
			new Sshd().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
