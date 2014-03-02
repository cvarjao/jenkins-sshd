package com.cvarjao.jenkins.sshd.api;

import org.apache.sshd.server.Command;

public interface JoinCommand extends Command {
	public Command getLeft();
	public void setLeft(Command left);
	public Command getRight();
	public void setRight(Command right);
}
