package com.cvarjao.jenkins.sshd.factory;

import java.io.IOException;

import org.apache.sshd.common.Session;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.file.FileSystemView;

import com.cvarjao.jenkins.sshd.file.JenkinsFileSystemView;

public class JenkinsFileSystemFactory implements FileSystemFactory {
	
	@Override
	public FileSystemView createFileSystemView(Session session) throws IOException {
		return new JenkinsFileSystemView(session);
	}
}
