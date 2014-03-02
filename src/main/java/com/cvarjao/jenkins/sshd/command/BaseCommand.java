package com.cvarjao.jenkins.sshd.command;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.ExitCallback;

public abstract class BaseCommand implements Command {
    protected InputStream in;
    protected OutputStream out;
    protected OutputStream err;
    protected ExitCallback callback;
    
    @Override
    public void setInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public void setOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void setErrorStream(OutputStream err) {
        this.err = err;
    }

	@Override
	public void setExitCallback(ExitCallback callback) {
		this.callback = callback;
	}

	@Override
	public void destroy() {
	}
	protected void delegateTo(Command command){
		command.setInputStream(in);
		command.setOutputStream(out);
		command.setErrorStream(err);
		command.setExitCallback(callback);
	}
}
