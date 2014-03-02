package com.cvarjao.jenkins.sshd.authetication;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublickeyAuthenticatorImpl implements PublickeyAuthenticator {
	private final Logger logger=LoggerFactory.getLogger(getClass());
	public static final String ANY_USER="any";
	private List<PublicKey> authorizedKeys=new ArrayList<>();
	private File authorizedKeysFile;
	
	
	public PublickeyAuthenticatorImpl(File file) {
		this.authorizedKeysFile=file;
		try {
			loadKeys();
		} catch (Exception e) {
			logger.error("error loading keys",e);
		}
	}
	public PublickeyAuthenticatorImpl() {
	}
	public void loadKeys() throws Exception{
		AuthorizedKeysDecoder decoder=new AuthorizedKeysDecoder();
        Scanner scanner2 = new Scanner(authorizedKeysFile);
		Scanner scanner = scanner2.useDelimiter("\n");
        while (scanner.hasNext()) {
            PublicKey key=decoder.decodePublicKey(scanner.next());
            authorizedKeys.add(key);
        }
        scanner2.close();
        scanner.close();
	}
	public List<PublicKey> getAuthorizedKeys() {
		return authorizedKeys;
	}
	public boolean authenticate(String username, PublicKey key, ServerSession session) {
		try {
			if (key instanceof RSAPublicKey) {
				String s1 = new String(encode(key, ANY_USER));
				if (logger.isDebugEnabled()){
					logger.debug(AuthorizedKeysDecoder.encodePublicKey(key, username));
				}
				for(PublicKey authKey:authorizedKeys){
					String s2 = new String(encode(authKey,ANY_USER));
					if (s1.equals(s2)){
						return true; // Returns true if the key matches our known key, this allows auth to proceed.
					}
				}
			}
		} catch (IOException e) {
			session.exceptionCaught(e);
		}
		return false; // Doesn't handle other key types currently.
	}

	// Converts a Java RSA PK to SSH2 Format.
	public static String encode(PublicKey authKey, String user) throws IOException {
		return AuthorizedKeysDecoder.encodePublicKey(authKey, user);
	}

	private static void write(byte[] str, OutputStream os) throws IOException {
		for (int shift = 24; shift >= 0; shift -= 8)
			os.write((str.length >>> shift) & 0xFF);
		os.write(str);
	}
}
