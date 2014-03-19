package com.cvarjao.jenkins.sshd.command;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.sshd.common.file.FileSystemAware;
import org.apache.sshd.common.file.FileSystemView;
import org.apache.sshd.common.file.SshFile;
import org.apache.sshd.common.scp.ScpHelper;
import org.apache.sshd.server.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cvarjao.jenkins.sshd.api.HasChangeDir;

public class TestCommand extends BaseCommand implements Runnable, FileSystemAware /*, SessionAware */ {
	public static final String COMMAND="test";
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private FileSystemView fileSystemView;
	private String[] args;
	
	public TestCommand(String[] args) {
		this.args=args;
	}
	
	@Override
	public void start(Environment env) throws IOException {
		new Thread(this).start();
	}

	@Override
	public void setFileSystemView(FileSystemView view) {
		this.fileSystemView=view;
	}

	@Override
	public void run() {
		logger.debug(COMMAND+" "+(args!=null?(StringUtils.join(args)):""));
		if (args.length==2 && "-d".equals(args[0])){
			SshFile sshFile=this.fileSystemView.getFile(args[1]);
			if (sshFile.doesExist()){
				callback.onExit(TrueCommand.EXIT_VALUE);
			}else{
				callback.onExit(FalseCommand.EXIT_VALUE);
			}
		}
	}
	
}
