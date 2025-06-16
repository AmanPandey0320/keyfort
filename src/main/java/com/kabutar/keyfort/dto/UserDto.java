package com.kabutar.keyfort.dto;

public class UserDto {
    private String username;
    private String password;
    private String clientId;
    private String responseType;
    private String redirectUri;
    private String scope;
    private String codeChallange;
    
    public UserDto() {}

	public UserDto(String username, String password, String clientId, String responseType, String redirectUri,
			String scope, String codeChallange) {
		super();
		this.username = username;
		this.password = password;
		this.clientId = clientId;
		this.responseType = responseType;
		this.redirectUri = redirectUri;
		this.scope = scope;
		this.codeChallange = codeChallange;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getCodeChallange() {
		return codeChallange;
	}

	public void setCodeChallange(String codeChallange) {
		this.codeChallange = codeChallange;
	}

}
