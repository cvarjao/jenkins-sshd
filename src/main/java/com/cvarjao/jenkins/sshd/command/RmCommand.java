package com.cvarjao.jenkins.sshd.command;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.sshd.common.file.FileSystemAware;
import org.apache.sshd.common.file.FileSystemView;
import org.apache.sshd.common.file.SshFile;
import org.apache.sshd.common.file.nativefs.NativeSshFile;
import org.apache.sshd.server.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RmCommand extends BaseCommand implements Runnable, FileSystemAware /*, SessionAware */ {
	public static final String COMMAND="rm";
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private FileSystemView fileSystemView;
	private String[] args;
	
	public RmCommand(String[] args) {
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
		int exitValue=FalseCommand.EXIT_VALUE;
		logger.debug(COMMAND+" "+(args!=null?(StringUtils.join(args)):""));
		if (args.length==2 && "-p".equals(args[0])){
			SshFile sshFile=this.fileSystemView.getFile(args[1]);
			if (sshFile instanceof NativeSshFile){
				File file=((NativeSshFile) sshFile).getPhysicalFile();
				file.delete();
				if (!file.exists()){
					exitValue=TrueCommand.EXIT_VALUE;
				}
			}
		}
		callback.onExit(exitValue);
	}
	
}
