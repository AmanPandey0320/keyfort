package com.kabutar.keyfort.dto;

public class TokenDto {
    private String token;
    private String clientSecret;
    
    public TokenDto() {}

    public TokenDto(String token, String clientSecret) {
        this.token = token;
        this.clientSecret = clientSecret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	@Override
	public String toString() {
		return "TokenDto [token=" + token + ", clientSecret=" + clientSecret + "]";
	}

    
}
