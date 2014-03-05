package com.cvarjao.jenkins.sshd.command;

import java.io.IOException;

import org.apache.sshd.server.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FalseCommand extends BaseCommand implements Runnable {
	public static final String COMMAND="false";
	public static final int EXIT_VALUE=1;
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public void start(Environment env) throws IOException {
		new Thread(this).start();
	}

	@Override
	public void run() {
        if (callback != null) {
            callback.onExit(EXIT_VALUE);
        }
	}
}
