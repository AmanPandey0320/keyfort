package com.kabutar.keyfort.util;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TokenGenerator {
    private static KeyGenerator keyGenerator;

    static {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error initializing KeyGenerator", e);
        }
    }

    public static String generateToken128(){
        return Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded()) + System.currentTimeMillis();
    }
}
