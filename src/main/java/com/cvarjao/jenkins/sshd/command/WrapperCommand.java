package com.cvarjao.jenkins.sshd.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

public class WrapperCommand implements Command {
	private Command wrapper;
	public WrapperCommand(Command wrapper) {
		this.wrapper=wrapper;
	}
	public void setInputStream(InputStream in) {
		wrapper.setInputStream(in);
	}
	public void setOutputStream(OutputStream out) {
		wrapper.setOutputStream(out);
	}
	public void setErrorStream(OutputStream err) {
		wrapper.setErrorStream(err);
	}
	public void setExitCallback(ExitCallback callback) {
		wrapper.setExitCallback(callback);
	}
	public void start(Environment env) throws IOException {
		wrapper.start(env);
	}
	public void destroy() {
		wrapper.destroy();
	}
	
}
