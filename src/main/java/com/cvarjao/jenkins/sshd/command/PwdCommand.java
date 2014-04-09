package com.cvarjao.jenkins.sshd.command;

import java.io.IOException;

import org.apache.sshd.common.file.FileSystemAware;
import org.apache.sshd.common.file.FileSystemView;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.SessionAware;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cvarjao.jenkins.sshd.api.HasChangeDir;

public class PwdCommand extends BaseCommand implements Runnable, FileSystemAware {
	public static final String COMMAND="pwd";
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private FileSystemView fileSystemView;
	
	public PwdCommand() {
	}
	
	@Override
	public void start(Environment env) throws IOException {
		new Thread(this).start();
	}



	@Override
	public void run() {
		String currentDir=((HasChangeDir) this.fileSystemView).getCurrentDir();
		logger.debug(COMMAND);
		
		try {
			this.out.write(currentDir.getBytes());
			this.out.flush();
			callback.onExit(TrueCommand.EXIT_VALUE);
		} catch (IOException e) {
			callback.onExit(FalseCommand.EXIT_VALUE, e.getMessage());
		}

	}

	@Override
	public void setFileSystemView(FileSystemView fileSystemView) {
		this.fileSystemView=fileSystemView;
	}
	
}
