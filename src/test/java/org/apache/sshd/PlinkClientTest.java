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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.ProcessBuilder.Redirect;
import java.security.Key;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;

import org.apache.sshd.common.KeyPairProvider;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.command.UnknownCommand;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.util.BaseTest;
import org.apache.sshd.util.BogusPublickeyAuthenticator;
import org.apache.sshd.util.PemUtil;
import org.apache.sshd.util.Utils;
import org.bouncycastle.openssl.PEMWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cvarjao.jenkins.sshd.command.FalseCommand;
import com.cvarjao.jenkins.sshd.command.SetCommand;
import com.cvarjao.jenkins.sshd.command.TrueCommand;

/**
 * TODO Add javadoc
 *
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public class PlinkClientTest extends BaseTest {

    private SshServer sshd;
    private int port;
    private String username="userx";
    
    @Before
    public void setUp() throws Exception {
        port = Utils.getFreePort();

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
    
    public static String convertToPem(Key cert) throws IOException{
    	StringWriter writer=new StringWriter();
        PEMWriter pw = new PEMWriter(writer);
        pw.writeObject(cert);
        pw.flush();
        pw.close();
        return writer.toString();
    }
    
    @Test
    public void testTrileadClient() throws IOException, InterruptedException, CertificateEncodingException{
    	KeyPair pair = Utils.createTestHostKeyProvider().loadKey(KeyPairProvider.SSH_RSA);
    	String pubkey=PemUtil.convertToPem(pair.getPrivate());
    	
    	File keyFile= new File("test.pem");
    	FileWriter writer=new FileWriter(keyFile);
    	writer.write(pubkey);
    	writer.close();
    	
    	int exitValue=-1;
		try {
			ProcessBuilder builder = new ProcessBuilder("plink", "-ssh", "localhost", "-P", Integer.toString(port), "-l", username, "-N", "-i", keyFile.getAbsolutePath(), "-batch", "true");
			builder.redirectError(Redirect.INHERIT);
			builder.redirectOutput(Redirect.INHERIT);
			builder.redirectInput(Redirect.INHERIT);
			Process process = builder.start();        
			exitValue = process.waitFor();
		} finally{
			keyFile.delete();
		}
        assertEquals(1, exitValue);
    }
}
