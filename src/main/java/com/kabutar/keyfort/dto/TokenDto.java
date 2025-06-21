package com.kabutar.keyfort.dto;

public class TokenDto {
    private String token;
    private String clientSecret;
    private String codeVerifier;
    private String grantType;
    
    public TokenDto() {}

    public TokenDto(String token, String clientSecret, String codeVerifier, String grantType) {
        this.token = token;
        this.clientSecret = clientSecret;
        this.codeVerifier = codeVerifier;
        this.grantType = grantType;
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
	

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	@Override
	public String toString() {
		return "TokenDto [token=" + token + ", clientSecret=" + clientSecret + ", codeVerifier=" + codeVerifier + ", grantType=" + grantType + "]";
	}

	

    
}
