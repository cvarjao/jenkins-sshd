package com.cvarjao.jenkins.sshd.api;

public interface HasChangeDir {
	public String getCurrentDir();
	
	public void setCurrentDir(String currDir);
}
