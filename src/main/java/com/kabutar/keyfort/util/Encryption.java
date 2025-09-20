package com.kabutar.keyfort.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {

    /**
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String withSHA3(String str) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        byte[] hashCode = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashCode);
    }

    /**
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b & 0xff));
        }
        return hexString.toString();
    }
}
