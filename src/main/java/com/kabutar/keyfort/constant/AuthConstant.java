package com.kabutar.keyfort.constant;

public class AuthConstant {

    public static String ROLE = "role";

    public static class TokenType{
        static public String AUTHORIZATION = "authorization";
        static public String ACCESS = "access";
        static public String REFRESH = "refresh";

    }

    // ALL TIME IN SECONDS
    public static class ExpiryTime{
        static public Integer ACCESS_TOKEN = 1800;
        static public Integer REFRESH_TOKEN = 21600;
        static public Integer AUTHZ_CODE = 300;

    }

    public static class ClaimType{
        static public String ROLE = "role";
        static public String CLIENT = "client";
    }
}
