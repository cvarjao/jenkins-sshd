package org.apache.sshd.util;

import java.io.IOException;
import java.io.StringWriter;
import java.security.Key;

import org.bouncycastle.openssl.PEMWriter;

public class PemUtil {
    public static String convertToPem(Key cert) throws IOException{
    	StringWriter writer=new StringWriter();
        PEMWriter pw = new PEMWriter(writer);
        pw.writeObject(cert);
        pw.flush();
        pw.close();
        return writer.toString();
    }
}
