package com.kabutar.keyfort.constant;

public class AuthConstant {

    public static String ROLE = "role";

    public static class TokenType {
        public static String AUTHORIZATION = "authorization";
        public static String ACCESS = "access";
        public static String REFRESH = "refresh";
    }

    // ALL TIME IN SECONDS
    public static class ExpiryTime {
        public static Integer ACCESS_TOKEN = 1800;
        public static Integer REFRESH_TOKEN = 21600;
        public static Integer AUTHZ_CODE = 300;
        public static Integer SESSION = 21600;
    }

    public static class ClaimType {
        public static String ROLE = "role";
        public static String SESSION = "session";
    }

    public static class CookieType {
        public static String ACCESS_TOKEN = "KF_ACCESS_TOKEN";
        public static String REFRESH_TOKEN = "KF_REFRESH_TOKEN";
        public static String SESSION_ID = "KF_SESSION_ID";
    }

    public static class GrantType {
        public static String AUTH_CODE = "authorization_code";
    }
}
