package com.kabutar.keyfort.dto;

public class TokenDto {
    private String token;
    private String clientSecret;
    private String codeVerifier;
    
    public TokenDto() {}

    public TokenDto(String token, String clientSecret, String codeVerifier) {
        this.token = token;
        this.clientSecret = clientSecret;
        this.codeVerifier = codeVerifier;
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
	
	

	public String getCodeVerifier() {
		return codeVerifier;
	}

	public void setCodeVerifier(String codeVerifier) {
		this.codeVerifier = codeVerifier;
	}

	@Override
	public String toString() {
		return "TokenDto [token=" + token + ", clientSecret=" + clientSecret + ", codeVerifier=" + codeVerifier + "]";
	}

	

    
}
