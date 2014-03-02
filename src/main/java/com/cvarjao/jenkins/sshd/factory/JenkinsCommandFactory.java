package com.cvarjao.jenkins.sshd.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.command.UnknownCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cvarjao.jenkins.sshd.api.JoinCommand;
import com.cvarjao.jenkins.sshd.command.AndCommand;
import com.cvarjao.jenkins.sshd.command.CdCommand;
import com.cvarjao.jenkins.sshd.command.ExecCommand;
import com.cvarjao.jenkins.sshd.command.SetCommand;
import com.cvarjao.jenkins.sshd.command.TrueCommand;
import com.cvarjao.jenkins.sshd.file.NativeFileUtil;

public class JenkinsCommandFactory implements CommandFactory {
	private final Logger logger=LoggerFactory.getLogger(getClass());
	private Pattern commandLinePattern=Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
	private CommandFactory scpCommandFactory=new ScpCommandFactory();
	
	public Command parseCommand(String command) {
		Matcher m=commandLinePattern.matcher(command);
		List<String> listArgs=new ArrayList<>();
		
		Command cmd=null;
		
		while (m.find()){
			String part=m.group();
			if ("&&".equals(part)){
				if (cmd==null){
					cmd=new AndCommand(createCommand(listArgs.toArray(new String[listArgs.size()])), null);
					listArgs.clear();
				}
			}else{
				listArgs.add(part);
			}
		}
		
		if (cmd instanceof JoinCommand){
			if (((JoinCommand) cmd).getRight()==null){
				((JoinCommand) cmd).setRight(createCommand(listArgs.toArray(new String[listArgs.size()])));
			}else{
				throw new RuntimeException("Something is not right!");
			}
		}else{
			cmd=createCommand(listArgs.toArray(new String[listArgs.size()]));
		}
		return cmd;
	}
	
	public Command createCommand(String[] commands) {
		if (TrueCommand.COMMAND.equals(commands[0])){
			return new TrueCommand();
		}else if (CdCommand.COMMAND.equals(commands[0])){
			return new CdCommand(commands.length>1?Arrays.copyOfRange(commands, 1, commands.length):new String[0]);
		}else if (SetCommand.COMMAND.equals(commands[0])){
			return new SetCommand(commands.length>1?Arrays.copyOfRange(commands, 1, commands.length):new String[0]);
		}else if (commands[0].equals("java")){
			commands[0]=NativeFileUtil.normalizeSeparateChar(System.getProperty("java.home")+"/bin/java.exe");
			/*
			if (commands[commands.length-1].equals("slave.jar")){
				commands[commands.length-1]="C:/Data/projects/jenkins-windows-sshd/workdir/apps_data/jenkins/slaves/localhost/slave.jar";
			}
			*/
			return new ExecCommand(commands);
		}
		
		return new UnknownCommand(StringUtils.join(commands, ' '));
	}
	
	@Override
	public Command createCommand(String command) {
		if (command.startsWith("scp")){
			return scpCommandFactory.createCommand(command);
		}
		
		Command cmd= parseCommand(command);
		if (cmd==null){
			cmd=new UnknownCommand(command);
		}
		return cmd;
	
	}

}
