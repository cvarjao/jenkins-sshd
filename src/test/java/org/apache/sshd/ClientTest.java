/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sshd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Properties;

import org.apache.sshd.client.UserAuth;
import org.apache.sshd.client.auth.UserAuthPublicKey;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.common.KeyPairProvider;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.io.mina.MinaServiceFactoryFactory;
import org.apache.sshd.common.util.NoCloseOutputStream;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.command.UnknownCommand;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.util.BaseTest;
import org.apache.sshd.util.BogusPublickeyAuthenticator;
import org.apache.sshd.util.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.putty.PuTTYKey;

import com.cvarjao.jenkins.sshd.Sshd;
import com.cvarjao.jenkins.sshd.command.FalseCommand;
import com.cvarjao.jenkins.sshd.command.SetCommand;
import com.cvarjao.jenkins.sshd.command.TrueCommand;
import com.cvarjao.jenkins.sshd.factory.JenkinsCommandFactory;
import com.cvarjao.jenkins.sshd.factory.JenkinsFileSystemFactory;
import com.cvarjao.jenkins.sshd.factory.JenkinsSessionFactory;
import com.cvarjao.jenkins.sshd.file.NativeFileUtil;
import com.trilead.ssh2.Connection;

/**
 * TODO Add javadoc
 *
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public class ClientTest extends BaseTest {

    private SshServer sshd;
    private int port;
    private String username="userx";
    
    @Before
    public void setUp() throws Exception {
    	port=22;
    	Properties props=new Properties();
    	props.setProperty(Sshd.SSHD_PORT, Integer.toString(port));
    	props.setProperty(Sshd.SSHD_ROOT_DIR, NativeFileUtil.normalizeSeparateChar(System.getProperty("user.dir"))+"/workDir/");
    	props.setProperty(Sshd.SSHD_HOME_DIR, "/");
        sshd = Sshd.createStub(props);       
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setPublickeyAuthenticator(new BogusPublickeyAuthenticator());
        sshd.start();
    }

    @After
    public void tearDown() throws Exception {
        if (sshd != null) {
            sshd.stop(true);
            Thread.sleep(50);
        }
    }

    public ChannelExec doExec(ClientSession session, String command) throws Exception {
        ChannelExec channel = session.createExecChannel(command);
        channel.setOut(new NoCloseOutputStream(System.out));
        channel.setErr(new NoCloseOutputStream(System.err));
        assertTrue(channel.open().await().isOpened());
        channel.waitFor(ClientChannel.EXIT_STATUS, 5000);
        channel.close(false).await();
        
        return channel;
    }
    @Test
    public void testSshClient() throws Exception {
        SshClient client = SshClient.setUpDefaultClient();
        client.setUserAuthFactories(Arrays.<NamedFactory<UserAuth>>asList(new UserAuthPublicKey.Factory()));
        client.start();
        
        ClientSession session = client.connect(username, "localhost", port).await().getSession();
        KeyPair pair = Utils.createTestHostKeyProvider().loadKey(KeyPairProvider.SSH_RSA);
        session.addPublicKeyIdentity(pair);
        session.auth().verify();
        //assertTrue(session.authPublicKey(username, pair).await().isSuccess());
        
        
        ChannelExec channel = doExec(session, "true");
        
        Integer exitStatus=channel.getExitStatus();
        assertNotNull(exitStatus);
        assertEquals((int)TrueCommand.EXIT_VALUE, (int)exitStatus);
        
        
        channel=doExec(session, "false");
        exitStatus=channel.getExitStatus();
        assertNotNull(exitStatus);
        assertEquals((int)FalseCommand.EXIT_VALUE, (int)exitStatus);
        
        channel=doExec(session, "java -version");
        exitStatus=channel.getExitStatus();
        assertNotNull(exitStatus);
        assertEquals((int)TrueCommand.EXIT_VALUE, (int)exitStatus);
        
        channel=doExec(session, "mkdir -p '/slaves/fritz' && cd '/slaves/fritz' && java -version");
        exitStatus=channel.getExitStatus();
        assertNotNull(exitStatus);
        assertEquals((int)TrueCommand.EXIT_VALUE, (int)exitStatus);
        
        session.close(false).await();
        
        client.stop();
    }
    
    public static void main(String[] args) throws Exception {
        SshClient.main(args);
    }
}
