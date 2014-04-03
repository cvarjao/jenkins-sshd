package com.cvarjao.jenkins.sshd.factory;

import static org.junit.Assert.*;

import org.apache.sshd.server.Command;
import org.junit.Test;

import com.cvarjao.jenkins.sshd.api.JoinCommand;
import com.cvarjao.jenkins.sshd.command.AndCommand;
import com.cvarjao.jenkins.sshd.command.CdCommand;
import com.cvarjao.jenkins.sshd.command.ExecCommand;
import com.cvarjao.jenkins.sshd.command.MkdirCommand;

public class JenkinsCommandFactoryTest {

	@Test
	public void testParseCommand() {
		JenkinsCommandFactory factory=new JenkinsCommandFactory();
		Command cmd=factory.parseCommand("mkdir '/slaves/fritz' && cd '/slaves/fritz' && java -version");
		assertNotNull(cmd);
		assertTrue(cmd instanceof AndCommand);
		JoinCommand joinCmd=(JoinCommand)cmd;
		
		assertNotNull(joinCmd.getLeft());
		assertTrue(joinCmd.getLeft() instanceof MkdirCommand);
		assertNotNull(joinCmd.getRight());
		assertTrue(joinCmd.getRight() instanceof AndCommand);
		joinCmd=(JoinCommand) joinCmd.getRight();
		assertNotNull(joinCmd.getLeft());
		assertTrue(joinCmd.getLeft() instanceof CdCommand);
		assertNotNull(joinCmd.getRight());
		assertTrue(joinCmd.getRight() instanceof ExecCommand);
		
	}

}
