package com.kabutar.keyfort.util;

import java.util.UUID;

public class IDGenerator {
    public static String generateUniqueId() {
        String uuid = UUID.randomUUID().toString();
        String timestamp = Long.toString(System.currentTimeMillis());

        StringBuilder sb = new StringBuilder();

        sb.append("KF/");
        sb.append(uuid);
        sb.append("/");
        sb.append(timestamp);

        return sb.toString();
    }
}
