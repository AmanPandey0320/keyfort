package com.kabutar.keyfort.util;

import java.util.UUID;

public class TokenGenerator {

    public static String generateToken128() {
        return generateToken() + System.currentTimeMillis();
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
