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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.command.UnknownCommand;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.util.BaseTest;
import org.apache.sshd.util.BogusPublickeyAuthenticator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.putty.PuTTYKey;

import com.cvarjao.jenkins.sshd.command.FalseCommand;
import com.cvarjao.jenkins.sshd.command.SetCommand;
import com.cvarjao.jenkins.sshd.command.TrueCommand;
import com.trilead.ssh2.Connection;

/**
 * TODO Add javadoc
 *
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public class TriledClientTest extends BaseTest {

    private SshServer sshd;
    private int port;
    private String username="userx";
    
    @Before
    public void setUp() throws Exception {
        port = 22;

        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        
        sshd.setCommandFactory(new CommandFactory() {
            public Command createCommand(String command) {
            	if (TrueCommand.COMMAND.equals(command)){
            		return new TrueCommand();
            	}else if (FalseCommand.COMMAND.equals(command)){
            		return new FalseCommand();
            	}else if (SetCommand.COMMAND.equals(command)){
            		return new SetCommand(new String[0]);
            	}
                return new UnknownCommand(command);
            }
        });
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

    @Test
    public void testTrileadClient() throws IOException, InterruptedException{
    	Connection connection;
    	connection = new Connection("localhost", port);
    	connection.connect();
    	
    	File key = new File("key.pem");
    	String pass="";
    	String username="userx";
    	
    	boolean isAuthenticated;
    	if(PuTTYKey.isPuTTYKeyFile(key)) {
    	       String openSshKey = new PuTTYKey(key, pass).toOpenSSH();
    	       isAuthenticated = connection.authenticateWithPublicKey(username, openSshKey.toCharArray(), pass);
    	   } else {
    	       isAuthenticated = connection.authenticateWithPublicKey(username, key, pass);
    	   }
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	int exitStatus=connection.exec("true", baos);
    	assertEquals(TrueCommand.EXIT_VALUE, exitStatus);
    	String s = baos.toString();
    	baos.reset();
    	exitStatus=connection.exec("set", baos);
    	s = baos.toString();
    	assertEquals(TrueCommand.EXIT_VALUE, exitStatus);
    	
    	baos.reset();
    	exitStatus=connection.exec("false", baos);
    	s = baos.toString();
    	assertEquals(FalseCommand.EXIT_VALUE, exitStatus);
    }
    public static void main(String[] args) throws Exception {
        SshClient.main(args);
    }
}
