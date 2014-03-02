package com.cvarjao.jenkins.sshd.command;

import java.io.IOException;

import org.apache.sshd.common.scp.ScpHelper;
import org.apache.sshd.server.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrueCommand extends BaseCommand {
	public static final String COMMAND="true";
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public void start(Environment env) throws IOException {
		int exitValue = ScpHelper.OK;
		String exitMessage = null;
		
        if (callback != null) {
            callback.onExit(exitValue, exitMessage);
        }
	}
}
