package com.kabutar.keyfort.cache.metadata;

import java.io.Serializable;

public class PkceMetadata implements Serializable {
	private String codeChallenge;

	public String getCodeChallenge() {
		return codeChallenge;
	}

	public void setCodeChallenge(String codeChallenge) {
		this.codeChallenge = codeChallenge;
	}
	
	
}
