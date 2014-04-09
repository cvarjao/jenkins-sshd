package com.cvarjao.jenkins.sshd.file;

import java.util.Map;

import org.apache.sshd.common.Session;
import org.apache.sshd.common.file.nativefs.NativeFileSystemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cvarjao.jenkins.sshd.api.HasChangeDir;

/***
 * Copied from NativeFileSystemView
 * @author clecio
 *
 */
public class JenkinsFileSystemView extends NativeFileSystemView implements HasChangeDir {
	private final Logger LOG = LoggerFactory.getLogger(JenkinsFileSystemView.class);
	private Session session;
	
	public JenkinsFileSystemView(Session session, Map<String, String> roots) {
		super(session.getUsername(), roots, ((HasChangeDir)session).getCurrentDir());
		this.session=session;
	}

	@Override
	public String getCurrentDir() {
		return ((HasChangeDir)session).getCurrentDir();
	}

	@Override
	public void setCurrentDir(String currDir) {
		((HasChangeDir)session).setCurrentDir(currDir);
	}
	
}