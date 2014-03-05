package com.cvarjao.jenkins.sshd.command;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.sshd.server.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetCommand extends BaseCommand implements Runnable {
	public static final String COMMAND="set";
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private String[] args;
	private Environment env;
	
	public SetCommand(String[] args) {
		this.args=args;
	}
	
	@Override
	public void start(Environment env) throws IOException {
		this.env=env;
		new Thread(this).start();
	}

	@Override
	public void run() {
		int exitValue=TrueCommand.EXIT_VALUE;
		String exitMessage=null;
		try {
			logger.debug(COMMAND+" "+(args!=null?(StringUtils.join(args)):""));			
			for (Entry<String, String> entry:env.getEnv().entrySet()){
				out.write((entry.getKey()+"="+entry.getValue()+"\n").getBytes());
			}
			out.flush();

		} catch (IOException e) {
			logger.info("Error running"+COMMAND, e);
			exitValue=FalseCommand.EXIT_VALUE;
			exitMessage=e.getMessage();
		}
		if (callback != null) {
		    callback.onExit(exitValue, exitMessage);
		}
	}
}
