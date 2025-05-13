package com.kabutar.keyfort.util;

import java.security.SecureRandom;

public class TokenGenerator {

    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateToken128() {
        return generateToken(16) + System.currentTimeMillis(); // 16 chars + timestamp
    }

    private static String generateToken(int length) {
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(ALLOWED_CHARS.length());
            token.append(ALLOWED_CHARS.charAt(index));
        }
        return token.toString();
    }
}
