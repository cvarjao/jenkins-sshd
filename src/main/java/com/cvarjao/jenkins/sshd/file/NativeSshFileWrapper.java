package com.cvarjao.jenkins.sshd.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.sshd.common.file.SshFile;

import com.cvarjao.sshd.file.nativefs.INativeSshFile;

public class NativeSshFileWrapper implements INativeSshFile {
	protected final INativeSshFile source;
	public NativeSshFileWrapper(INativeSshFile iNativeSshFile) {
		this.source=iNativeSshFile;
	}
	public String getAbsolutePath() {
		return source.getAbsolutePath();
	}

	public String getName() {
		return source.getName();
	}

	public Map<Attribute, Object> getAttributes(boolean followLinks) throws IOException {
		return source.getAttributes(followLinks);
	}

	public void setAttributes(Map<Attribute, Object> attributes) throws IOException {
		source.setAttributes(attributes);
	}

	public Object getAttribute(Attribute attribute, boolean followLinks) throws IOException {
		return source.getAttribute(attribute, followLinks);
	}

	public void setAttribute(Attribute attribute, Object value) throws IOException {
		source.setAttribute(attribute, value);
	}

	public String readSymbolicLink() throws IOException {
		return source.readSymbolicLink();
	}

	public String getOwner() {
		return source.getOwner();
	}

	public boolean isDirectory() {
		return source.isDirectory();
	}

	public boolean isFile() {
		return source.isFile();
	}

	public boolean doesExist() {
		return source.doesExist();
	}

	public boolean isReadable() {
		return source.isReadable();
	}

	public boolean isWritable() {
		return source.isWritable();
	}

	public boolean isExecutable() {
		return source.isExecutable();
	}

	public boolean isRemovable() {
		return source.isRemovable();
	}

	public SshFile getParentFile() {
		return source.getParentFile();
	}

	public long getLastModified() {
		return source.getLastModified();
	}

	public boolean setLastModified(long time) {
		return source.setLastModified(time);
	}

	public long getSize() {
		return source.getSize();
	}

	public boolean mkdir() {
		return source.mkdir();
	}

	public boolean delete() {
		return source.delete();
	}

	public boolean create() throws IOException {
		return source.create();
	}

	public void truncate() throws IOException {
		source.truncate();
	}

	public boolean move(SshFile destination) {
		return source.move(destination);
	}

	public List<SshFile> listSshFiles() {
		return source.listSshFiles();
	}

	public OutputStream createOutputStream(long offset) throws IOException {
		return source.createOutputStream(offset);
	}

	public InputStream createInputStream(long offset) throws IOException {
		return source.createInputStream(offset);
	}

	public void handleClose() throws IOException {
		source.handleClose();
	}
	@Override
	public File getNativeFile() {
		return source.getNativeFile();
	}
}
