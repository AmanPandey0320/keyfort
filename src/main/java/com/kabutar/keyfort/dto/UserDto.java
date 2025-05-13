package com.kabutar.keyfort.dto;

public class UserDto {
    private String username;
    private String password;
    private String clientId;
    
    public UserDto() {}

    public UserDto(String username, String password, String clientId) {
        this.username = username;
        this.password = password;
        this.clientId = clientId;
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
    
    
}
