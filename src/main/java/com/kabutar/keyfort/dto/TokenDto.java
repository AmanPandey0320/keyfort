package com.kabutar.keyfort.dto;

public class TokenDto {
    private String code;

    public TokenDto(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
