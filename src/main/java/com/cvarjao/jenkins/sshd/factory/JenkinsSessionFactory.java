package com.cvarjao.jenkins.sshd.factory;

import org.apache.sshd.common.io.IoSession;
import org.apache.sshd.common.session.AbstractSession;
import org.apache.sshd.server.ServerFactoryManager;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.session.SessionFactory;

import com.cvarjao.jenkins.sshd.api.HasChangeDir;

public class JenkinsSessionFactory extends SessionFactory {
	public JenkinsSessionFactory() {
		super();
	}

	@Override
	protected AbstractSession doCreateSession(IoSession ioSession) throws Exception {
		return new JenkinsServerSession(server, ioSession);
	}

	public static class JenkinsServerSession extends ServerSession implements HasChangeDir{
		private static final AttributeKey<String> CURRENT_DIR = new AttributeKey<String>();
		public JenkinsServerSession(ServerFactoryManager server, IoSession ioSession) throws Exception {
			super(server, ioSession);
		}
		@Override
		public String getCurrentDir() {
			return getAttribute(CURRENT_DIR);
		}

		@Override
		public void setCurrentDir(String currDir) {
			setAttribute(CURRENT_DIR, currDir);
		}
	}
}
