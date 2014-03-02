package com.cvarjao.jenkins.sshd.command;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.sshd.common.scp.ScpHelper;
import org.apache.sshd.server.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetCommand extends BaseCommand {
	public static final String COMMAND="set";
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private String[] args;
	
	public SetCommand(String[] args) {
		this.args=args;
	}
	
	@Override
	public void start(Environment env) throws IOException {
		logger.debug(COMMAND+" "+(args!=null?(StringUtils.join(args)):""));
		int exitValue = ScpHelper.OK;
		String exitMessage = null;
		
		
		for (Entry<String, String> entry:env.getEnv().entrySet()){
			out.write((entry.getKey()+"="+entry.getValue()+"\n").getBytes());
		}
		out.flush();
        if (callback != null) {
            callback.onExit(exitValue, exitMessage);
        }
	}
}
