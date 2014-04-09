package com.cvarjao.jenkins.sshd.factory;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.sshd.common.Session;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.file.FileSystemView;

import com.cvarjao.jenkins.sshd.api.HasChangeDir;
import com.cvarjao.jenkins.sshd.file.JenkinsFileSystemView;

public class JenkinsFileSystemFactory implements FileSystemFactory {
	private String defaultHomeDir;
	
	public JenkinsFileSystemFactory(String defaultHomeDir) {
		this.defaultHomeDir=defaultHomeDir;
	}
    private static Map<String, String> getAllRoots() {
        Map<String, String> roots = new LinkedHashMap<String, String>();
        for (File file : File.listRoots()) {
            if (file.exists()) {
                String root = file.toString();
                String name = root.substring(0, root.length() - 1);
                roots.put(name, root);
            }
        }
        return roots;
    }
	@Override
    public FileSystemView createFileSystemView(Session session) {
        Map<String, String> roots = getAllRoots();
        if (((HasChangeDir)session).getCurrentDir()==null){
        	((HasChangeDir)session).setCurrentDir(computeRootDir(session.getUsername()));
        }
        return new JenkinsFileSystemView(session, roots);
    }

	private String computeRootDir(String username) {
		return this.defaultHomeDir;
	}
}
