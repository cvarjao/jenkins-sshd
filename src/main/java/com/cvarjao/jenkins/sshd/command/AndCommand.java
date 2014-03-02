package com.cvarjao.jenkins.sshd.command;

import java.io.IOException;

import org.apache.sshd.common.file.FileSystemAware;
import org.apache.sshd.common.file.FileSystemView;
import org.apache.sshd.common.scp.ScpHelper;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.SessionAware;
import org.apache.sshd.server.session.ServerSession;

import com.cvarjao.jenkins.sshd.api.JoinCommand;

public class AndCommand extends TrueCommand implements JoinCommand, FileSystemAware, SessionAware {
	private Command left;
	private Command right;

	public AndCommand(Command left, Command right) {
		this.left=left;
		this.right=right;
	}
	@Override
	public Command getLeft() {
		return this.left;
	}
	@Override
	public void setLeft(Command left) {
		this.left = left;
	}
	@Override
	public Command getRight() {
		return this.right;
	}
	@Override
	public void setRight(Command right) {
		this.right = right;
	}
	
	public void runCommand(Command command, Environment env, ExitCallback argExitCallback) throws IOException {
		command.setInputStream(in);
		command.setOutputStream(out);
		command.setErrorStream(err);
		command.setExitCallback(argExitCallback);
		command.start(env);
	}
	
	@Override
	public void start(final Environment env) throws IOException {
		runCommand(this.left, env, new ExitCallback(){
			@Override
			public void onExit(int exitValue) {
				onExit(exitValue, null);
			}

			@Override
			public void onExit(int exitValue, String exitMessage) {
				if (exitValue==ScpHelper.OK){
					try {
						runCommand(right, env, callback);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					callback.onExit(exitValue, exitMessage);
				}
			}
			
		});
		
	}
	@Override
	public void setFileSystemView(FileSystemView view) {
		if (this.left instanceof FileSystemAware) ((FileSystemAware) this.left).setFileSystemView(view);
		if (this.right instanceof FileSystemAware) ((FileSystemAware) this.right).setFileSystemView(view);
	}
	@Override
	public void setSession(ServerSession session) {
		if (this.left instanceof SessionAware) ((SessionAware) this.left).setSession(session);
		if (this.right instanceof SessionAware) ((SessionAware) this.right).setSession(session);
	}
	@Override
	public void destroy() {
		this.left.destroy();
		this.right.destroy();
		super.destroy();
	}
}