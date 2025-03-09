package com.kabutar.keyfort.Entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Client {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String clientId;
	private String clientSecret;
	private String grantType;
	private String redirectUri;

	@OneToMany(mappedBy = "client")
	private List<User> users;
	
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public String getGrantType() {
		return grantType;
	}
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}
	public String getRedirectUri() {
		return redirectUri;
	}
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "Client [clientId=" + clientId + ", clientSecret=" + clientSecret + ", grantType=" + grantType
				+ ", redirectUri=" + redirectUri + "]";
	}
	
	
	
	
}
