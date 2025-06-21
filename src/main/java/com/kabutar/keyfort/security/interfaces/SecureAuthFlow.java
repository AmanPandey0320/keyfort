package com.kabutar.keyfort.security.interfaces;

public interface SecureAuthFlow {
	void init(String session, String challange);
	boolean verify(String session, String code) throws Exception;
}
