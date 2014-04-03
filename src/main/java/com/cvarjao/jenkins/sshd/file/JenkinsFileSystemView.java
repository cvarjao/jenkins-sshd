package com.cvarjao.jenkins.sshd.file;

import java.io.File;

import org.apache.sshd.common.Session;
import org.apache.sshd.common.Session.AttributeKey;
import org.apache.sshd.common.file.SshFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cvarjao.jenkins.sshd.api.HasChangeDir;

/***
 * Copied from NativeFileSystemView
 * @author clecio
 *
 */
public class JenkinsFileSystemView extends org.apache.sshd.common.file.nativefs.NativeFileSystemView implements HasChangeDir {
    private final Logger LOG = LoggerFactory.getLogger(JenkinsFileSystemView.class);
    private static final AttributeKey<String> CURRENT_DIR = new AttributeKey<String>();

    private Session session;
    private boolean caseInsensitive = false;
    private String virtualHomeDir;
    private String physicalRootDir;
    
    public JenkinsFileSystemView(Session session, String physicalDir, String homeDir) {
		super(session.getUsername(), false);
        this.caseInsensitive=false;
		this.session=session;
		this.physicalRootDir=physicalDir;
		this.virtualHomeDir=homeDir;
		if (this.session.getAttribute(CURRENT_DIR)==null){
			this.session.setAttribute(CURRENT_DIR, getVirtualUserDir());
		}
		LOG.debug("Created File System View. rootDir:"+physicalRootDir+" homeDir:"+virtualHomeDir);
	}

	/**
     * Get file object.
     */
    @Override
    public SshFile getFile(String file) {
    	if (LOG.isDebugEnabled()){
    		LOG.debug("CurrentDir:"+getCurrentDir()+" file: "+file);
    	}
        return getFile(getCurrentDir(), file);
    }

    /**
     * Returns the physical user directory, for accessing the according files or directories.
     *  
     * @return The physical user directory.
     */
    @Override
    public String getPhysicalUserDir() {
    	return this.physicalRootDir;
	}

	/**
	 * Returns the virtual user directory.
	 * The will be shown in sftp client. It is relative to the physical user directory. 
	 * 
	 * @return The virtual user directory.
	 */
    @Override
	public String getVirtualUserDir() {
		return this.virtualHomeDir;
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
	
}