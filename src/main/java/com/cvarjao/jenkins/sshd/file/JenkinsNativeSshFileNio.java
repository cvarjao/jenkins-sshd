package com.cvarjao.jenkins.sshd.file;

import java.io.File;

import org.apache.sshd.common.file.nativefs.NativeFileSystemView;
import org.apache.sshd.common.file.nativefs.NativeSshFileNio;

public class JenkinsNativeSshFileNio extends NativeSshFileNio {
	public JenkinsNativeSshFileNio(NativeFileSystemView nativeFileSystemView, String fileName, File file, String userName) {
		super(nativeFileSystemView, fileName, file, userName);
	}

}
