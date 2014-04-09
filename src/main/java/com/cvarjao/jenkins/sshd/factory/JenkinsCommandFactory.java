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
import com.cvarjao.jenkins.sshd.command.FalseCommand;
import com.cvarjao.jenkins.sshd.command.MkdirCommand;
import com.cvarjao.jenkins.sshd.command.PwdCommand;
import com.cvarjao.jenkins.sshd.command.RmCommand;
import com.cvarjao.jenkins.sshd.command.SetCommand;
import com.cvarjao.jenkins.sshd.command.TestCommand;
import com.cvarjao.jenkins.sshd.command.TrueCommand;
import com.cvarjao.jenkins.sshd.file.NativeFileUtil;

public class JenkinsCommandFactory implements CommandFactory {
	private final Logger logger=LoggerFactory.getLogger(getClass());
	private Pattern commandLinePattern=Pattern.compile("\\s+\\Q&&\\E\\s+");
	private Pattern commandArgsPattern=Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
	private CommandFactory scpCommandFactory=new ScpCommandFactory();
	
	
	public Command joinCommand(Command cmd, Command newCmd){
		Command retCmd=newCmd;
		if (cmd instanceof JoinCommand){
			retCmd=new AndCommand(((JoinCommand) cmd).getRight(), newCmd);
			((JoinCommand) cmd).setRight(retCmd);
		}else if (cmd!=null && newCmd!=null){
			retCmd=new AndCommand(cmd, newCmd);
		}
		return retCmd;
	}
	public Command joinCommand(Command cmd, CharSequence newCmdStr){
		List<String> args=new ArrayList<>();
		Matcher m2=commandArgsPattern.matcher(newCmdStr);
		while (m2.find()){
			String arg=m2.group();
			if ((arg.charAt(0) == '\'' || arg.charAt(0) == '\"') && (arg.charAt(0) == arg.charAt(arg.length()-1))){
				args.add(arg.substring(1, arg.length()-1));
			}else{
				args.add(arg);
			}
		}
		Command newCmd=createCommand(args.toArray(new String[args.size()]));
		if (cmd==null){
			return newCmd;
		}
		return joinCommand(cmd, newCmd);
	}
	public Command parseCommand(String command) {
		Matcher m=commandLinePattern.matcher(command);
	
		Command firstJoin=null;
		Command cmd=null;
		StringBuffer buffer=new StringBuffer();
		
		while (m.find()){
			buffer.setLength(0);
			m.appendReplacement(buffer, "");
			cmd=joinCommand(cmd, buffer);
			//Save the first JoinCommand as return command 
			if (firstJoin==null && cmd instanceof JoinCommand){
				firstJoin=cmd;
			}
		}
		buffer.setLength(0);
		m.appendTail(buffer);
		if(buffer.length()>0){
			cmd=joinCommand(cmd, buffer);
		}
		if (firstJoin!=null) return firstJoin;
		return cmd;
	}
	
	public Command createCommand(String[] commands) {
		if (TrueCommand.COMMAND.equals(commands[0])){
			return new TrueCommand();
		}else if (FalseCommand.COMMAND.equals(commands[0])){
			return new FalseCommand();
		}else if (PwdCommand.COMMAND.equals(commands[0])){
			return new PwdCommand();
		}else if (CdCommand.COMMAND.equals(commands[0])){
			return new CdCommand(commands.length>1?Arrays.copyOfRange(commands, 1, commands.length):new String[0]);
		}else if (SetCommand.COMMAND.equals(commands[0])){
			return new SetCommand(commands.length>1?Arrays.copyOfRange(commands, 1, commands.length):new String[0]);
		}else if (TestCommand.COMMAND.equals(commands[0])){
			return new TestCommand(commands.length>1?Arrays.copyOfRange(commands, 1, commands.length):new String[0]);
		}else if (MkdirCommand.COMMAND.equals(commands[0])){
			return new MkdirCommand(commands.length>1?Arrays.copyOfRange(commands, 1, commands.length):new String[0]);
		}else if (RmCommand.COMMAND.equals(commands[0])){
			return new RmCommand(commands.length>1?Arrays.copyOfRange(commands, 1, commands.length):new String[0]);
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
