package com.cvarjao.jenkins.sshd.command;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.sshd.common.file.FileSystemAware;
import org.apache.sshd.common.file.FileSystemView;
import org.apache.sshd.common.file.SshFile;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cvarjao.jenkins.sshd.api.HasChangeDir;
import com.cvarjao.jenkins.sshd.file.NativeFileUtil;
import com.cvarjao.sshd.file.nativefs.INativeSshFile;

public class ExecCommand extends BaseCommand implements Command, FileSystemAware, Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(ExecCommand.class);
	public static final int DEFAULT_BUFFER_SIZE = 8192;
	private final int bufferSize = DEFAULT_BUFFER_SIZE;

	private String[] cmds;
	private FileSystemView fileSystemView;
	private Thread streamHandler;
	private Process process;
	private InputStream shellIn;
	private OutputStream shellOut;
	private InputStream shellErr;

	public ExecCommand(String[] cmds) {
		this.cmds = cmds;
	}

	@Override
	public void start(Environment env) throws IOException {
		cmds[0] = NativeFileUtil.normalizeSeparateChar(cmds[0]);
		ProcessBuilder builder = new ProcessBuilder(cmds);
		if (env != null) {
			try {
				builder.environment().putAll(env.getEnv());
			} catch (Exception e) {
				LOG.info("Could not set environment for command", e);
			}
		}
		LOG.info("Starting shell with command: '{}' and env: {}", builder.command(), builder.environment());

		SshFile sshFile = this.fileSystemView.getFile(((HasChangeDir) this.fileSystemView).getCurrentDir());
		File nativeFile = ((INativeSshFile) sshFile).getNativeFile();
		builder.directory(nativeFile);
		process = builder.start();
		this.shellIn = process.getInputStream();
		this.shellOut = process.getOutputStream();
		this.shellErr = process.getErrorStream();
		streamHandler = new Thread(this, "pumpStreams");
		streamHandler.start();
		//while (isAlive()){}
	}

	public boolean isAlive() {
		try {
			process.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}

	public int exitValue() {
		try {
			return process.waitFor();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setFileSystemView(FileSystemView fileSystemView) {
		this.fileSystemView = fileSystemView;
	}

	protected void pumpStreams() {
        try {
            // Use a single thread to correctly sequence the output and error streams.
            // If any bytes are available from the output stream, send them first, then
            // check the error stream, or wait until more data is available.
            byte[] buffer = new byte[bufferSize];
            for (;;) {
                if (pumpStream(in, shellOut, buffer)) {
                    continue;
                }
                if (pumpStream(shellIn, out, buffer)) {
                    continue;
                }
                if (pumpStream(shellErr, err, buffer)) {
                    continue;
                }
                if (!isAlive()) {
                    callback.onExit(exitValue());
                    return;
                }
                // Sleep a bit.  This is not very good, as it consumes CPU, but the
                // input streams are not selectable for nio, and any other blocking
                // method would consume at least two threads
                Thread.sleep(1);
            }
        } catch (Exception e) {
            callback.onExit(exitValue());
        }
    }

	private boolean pumpStream(InputStream in, OutputStream out, byte[] buffer) throws IOException {
		int available = in.available();
		if (available > 0) {
			int len = in.read(buffer);
			if (len > 0) {
				//System.err.write(buffer, 0, len);
				out.write(buffer, 0, len);
				out.flush();
				return true;
			}
		} else if (available == -1) {
			out.close();
		}
		return false;
	}
	@Override
	public void destroy() {
		
	}
	@Override
	public void run() {
		pumpStreams();
	}
}
