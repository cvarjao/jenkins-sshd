package com.cvarjao.jenkins.sshd.command;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.sshd.common.file.FileSystemAware;
import org.apache.sshd.common.file.FileSystemView;
import org.apache.sshd.common.scp.ScpHelper;
import org.apache.sshd.server.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cvarjao.jenkins.sshd.api.HasChangeDir;

public class CdCommand extends BaseCommand implements Runnable, FileSystemAware {
	public static final String COMMAND="cd";
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private String[] args;
	private FileSystemView fileSystemView;
	
	public CdCommand(String[] args) {
		this.args=args;
	}
	
	@Override
	public void start(Environment env) throws IOException {
		new Thread(this).start();
	}



	@Override
	public void run() {
		logger.debug(COMMAND+" "+(args!=null?(StringUtils.join(args)):""));
		
		int exitValue = ScpHelper.OK;
		String exitMessage = null;
		String dir=args[args.length-1];
		if (dir.startsWith("\"") && dir.endsWith("\"")){
			dir=dir.substring(1, dir.length()-1);
		}
		((HasChangeDir) this.fileSystemView).setCurrentDir(dir);
		if (callback != null) {
            callback.onExit(exitValue, exitMessage);
        }
	}
	
	@Override
	public void setFileSystemView(FileSystemView fileSystemView) {
		this.fileSystemView = fileSystemView;
	}
}
