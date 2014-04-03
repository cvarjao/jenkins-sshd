package com.cvarjao.jenkins.sshd;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;
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
	public static final String SSHD_PORT= "sshd.port";
	public static final String SSHD_ROOT_DIR = "sshd.root.dir";
	public static final String SSHD_HOME_DIR = "sshd.home.dir";
	
	private SshServer sshd;

	public static SshServer create(Properties props) throws IOException {
		SshServer _sshd =createStub(props);
		_sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		_sshd.setPublickeyAuthenticator(new PublickeyAuthenticatorImpl(new File("authorized_keys")));
		return _sshd;
	}
	public static SshServer createStub(Properties props) throws IOException {
		int _port = Integer.parseInt(props.getProperty(SSHD_PORT, "22"));

		File rootDir=new File(props.getProperty(SSHD_ROOT_DIR,"workDir"));
		String homeDir=props.getProperty(SSHD_HOME_DIR, "/");
		String physycalHomeDir=new File(rootDir.getAbsolutePath()+homeDir).getAbsolutePath();
		physycalHomeDir=physycalHomeDir.replaceAll(Pattern.quote("\\"), "/");
		physycalHomeDir=physycalHomeDir.replaceAll("/+", "/");
		physycalHomeDir=physycalHomeDir.replaceAll("/", "\\\\");
		rootDir.mkdirs();
		
		SshServer _sshd = SshServer.setUpDefaultServer();
		_sshd.setPort(_port);

		_sshd.getProperties().put(SshServer.WELCOME_BANNER, WELCOME);
		_sshd.getProperties().put(SshServer.AUTH_METHODS, "publickey");
		_sshd.setSessionFactory(new JenkinsSessionFactory());
		//sshd.setSubsystemFactories(Arrays.<NamedFactory<Command>>asList(new SftpSubsystem.Factory()));
		_sshd.setFileSystemFactory(new JenkinsFileSystemFactory(props.getProperty(SSHD_ROOT_DIR), homeDir));
		_sshd.setCommandFactory(new JenkinsCommandFactory());
		_sshd.setShellFactory(new ProcessShellFactory(new String[] {props.getProperty("shell.cmd", "cmd.exe"), "/K", "cd /d "+physycalHomeDir+""}, EnumSet.of(ProcessShellFactory.TtyOptions.Echo, ProcessShellFactory.TtyOptions.ICrNl, ProcessShellFactory.TtyOptions.ONlCr)));
		//sshd.setIoServiceFactoryFactory(new MinaServiceFactoryFactory());
		//sshd.setIoServiceFactoryFactory(new Nio2ServiceFactoryFactory());
		return _sshd;
		
	}
	public void start() throws IOException {
		sshd=create(System.getProperties());
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
