package com.kabutar.keyfort.data.entity.client;

import jakarta.persistence.*;

import java.util.List;

import com.kabutar.keyfort.data.entity.Dimension;
import com.kabutar.keyfort.data.entity.User;

@Entity
public class Client {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String clientId;
	private String clientSecret;
	private String grantType;
	private String redirectUri;
	private String name;

	@OneToMany(mappedBy = "client")
	private List<User> users;

	@ManyToOne
	@JoinColumn(name = "dimension_id", nullable = false)
	private Dimension dimension;

	public Dimension getDimension() {
		return dimension;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

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
