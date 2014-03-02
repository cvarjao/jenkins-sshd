package com.cvarjao.jenkins.sshd.factory;

import org.apache.sshd.common.io.IoSession;
import org.apache.sshd.common.session.AbstractSession;
import org.apache.sshd.server.session.SessionFactory;

public class JenkinsSessionFactory extends SessionFactory {
	public JenkinsSessionFactory() {
		super();
	}
	@Override
	public void sessionCreated(IoSession ioSession) throws Exception {
		super.sessionCreated(ioSession);
	}
	@Override
	public void sessionClosed(IoSession ioSession) throws Exception {
		super.sessionClosed(ioSession);
	}
	@Override
	protected AbstractSession doCreateSession(IoSession ioSession) throws Exception {
		return super.doCreateSession(ioSession);
	}
	@Override
	protected AbstractSession createSession(IoSession ioSession) throws Exception {
		return super.createSession(ioSession);
	}
}
