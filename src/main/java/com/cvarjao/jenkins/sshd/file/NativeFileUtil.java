package com.cvarjao.jenkins.sshd.file;

import org.apache.sshd.common.file.nativefs.NativeSshFile;

public class NativeFileUtil {
    /**
     * Normalize separate character. Separate character should be '/' always.
     */
    public final static String normalizeSeparateChar(final String pathName) {
    	return NativeSshFile.normalizeSeparateChar(pathName);
    }
}
