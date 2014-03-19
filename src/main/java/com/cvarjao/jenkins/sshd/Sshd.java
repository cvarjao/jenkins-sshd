package com.cvarjao.jenkins.sshd;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.regex.Pattern;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private final static Logger logger=LoggerFactory.getLogger(Sshd.class);
	private static final String WELCOME = "Welcome to my server";
	private int port;
	private SshServer sshd;

	public void start() throws IOException {
		port = Integer.parseInt(System.getProperty("sshd.port", "22"));

		File rootDir=new File(System.getProperty("sshd.root.dir","workDir"));
		String homeDir=System.getProperty("sshd.home.dir", "/");
		String physycalHomeDir=new File(rootDir.getAbsolutePath()+homeDir).getAbsolutePath();
		physycalHomeDir=physycalHomeDir.replaceAll(Pattern.quote("\\"), "/");
		physycalHomeDir=physycalHomeDir.replaceAll("/+", "/");
		physycalHomeDir=physycalHomeDir.replaceAll("/", "\\\\");
		rootDir.mkdirs();
		
		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(port);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		sshd.setPublickeyAuthenticator(new PublickeyAuthenticatorImpl(new File("authorized_keys")));
		sshd.getProperties().put(SshServer.WELCOME_BANNER, WELCOME);
		sshd.getProperties().put(SshServer.AUTH_METHODS, "publickey");
		sshd.setSessionFactory(new JenkinsSessionFactory());
		//sshd.setSubsystemFactories(Arrays.<NamedFactory<Command>>asList(new SftpSubsystem.Factory()));
		sshd.setFileSystemFactory(new JenkinsFileSystemFactory(System.getProperty("sshd.root.dir"), homeDir));
	    sshd.setCommandFactory(new JenkinsCommandFactory());
        sshd.setShellFactory(new ProcessShellFactory(new String[] {System.getProperty("shell.cmd", "cmd.exe"), "/K", "cd /d "+physycalHomeDir+""}, EnumSet.of(ProcessShellFactory.TtyOptions.Echo, ProcessShellFactory.TtyOptions.ICrNl, ProcessShellFactory.TtyOptions.ONlCr)));
		//sshd.setIoServiceFactoryFactory(new MinaServiceFactoryFactory());
		//sshd.setIoServiceFactoryFactory(new Nio2ServiceFactoryFactory());
		
		sshd.start();
	}
	public static void main(String[] args) {
		try {
			//System.setProperty(IoServiceFactory.class.getName(), MinaServiceFactory.class.getName());
			//org.apache.sshd.SshServer.main(new String[]{"-p", "22", "-io", "nio2"});
			new Sshd().start();
			while(true){
				try {
					Thread.sleep(Integer.MAX_VALUE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			logger.error("fatal error",e);
		}
	}
}
