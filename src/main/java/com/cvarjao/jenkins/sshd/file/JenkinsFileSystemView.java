package com.cvarjao.jenkins.sshd.file;

import java.io.File;

import org.apache.sshd.common.Session;
import org.apache.sshd.common.Session.AttributeKey;
import org.apache.sshd.common.file.SshFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cvarjao.jenkins.sshd.api.HasChangeDir;
import com.cvarjao.sshd.file.nativefs.INativeSshFile;
import com.cvarjao.sshd.file.nativefs.NativeFileSystemView;
import com.cvarjao.sshd.file.nativefs.NativeSshFile;

/***
 * Copied from NativeFileSystemView
 * @author clecio
 *
 */
public class JenkinsFileSystemView extends NativeFileSystemView implements HasChangeDir {
    private final Logger LOG = LoggerFactory.getLogger(JenkinsFileSystemView.class);
    private static final AttributeKey<String> CURRENT_DIR = new AttributeKey<String>();

    private Session session;
    private boolean caseInsensitive = false;

    public JenkinsFileSystemView(Session session) {
		super(session.getUsername(), false);
        this.caseInsensitive=false;
		this.session=session;
		if (this.session.getAttribute(CURRENT_DIR)==null){
			this.session.setAttribute(CURRENT_DIR, getVirtualUserDir());
		}
	}

	/**
     * Get file object.
     */
    @Override
    public SshFile getFile(String file) {
        return getFile(getCurrentDir(), file);
    }

    /**
     * Returns the physical user directory, for accessing the according files or directories.
     *  
     * @return The physical user directory.
     */
    @Override
    public String getPhysicalUserDir() {
    	return "C:/Data/projects/jenkins-windows-sshd/workdir/";
	}

	/**
	 * Returns the virtual user directory.
	 * The will be shown in sftp client. It is relative to the physical user directory. 
	 * 
	 * @return The virtual user directory.
	 */
    @Override
	public String getVirtualUserDir() {
		return "/apps_nt/jenkins";
	}
	
	public boolean isCaseInsensitive(){
		return caseInsensitive;
	}
	
	@Override
	public String getCurrentDir() {
		return session.getAttribute(CURRENT_DIR);
	}
	
	@Override
	public void setCurrentDir(String currDir) {
		this.session.setAttribute(CURRENT_DIR, currDir);
	}
	
	@Override
	public INativeSshFile createNativeSshFile(String fileName2, File fileObj, String userName2) {
		return new NativeSshFileWrapper(super.createNativeSshFile(fileName2, fileObj, userName2));
	}
}