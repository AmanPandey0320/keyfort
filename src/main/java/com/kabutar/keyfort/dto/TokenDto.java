package com.kabutar.keyfort.dto;

public class TokenDto {
    private String token;
    private String grantType;

    public TokenDto(String token, String grantType) {
        this.token = token;
        this.grantType = grantType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    @Override
    public String toString() {
        return "TokenDto{" +
                "token='" + token + '\'' +
                ", grantType='" + grantType + '\'' +
                '}';
    }
}
