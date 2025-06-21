package com.kabutar.keyfort.security.flow;

import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

import com.kabutar.keyfort.cache.intefaces.CacheRepository;
import com.kabutar.keyfort.constant.CacheConstant;
import com.kabutar.keyfort.security.interfaces.SecureAuthFlow;
import com.kabutar.keyfort.util.Encryption;

@Component
public class PCKEFlow implements SecureAuthFlow {
	
	private CacheRepository cacheRepository;
	private String store;
	
	

	public PCKEFlow(CacheRepository cacheRepository) {
		super();
		this.cacheRepository = cacheRepository;
		this.store = CacheConstant.CacheStore.PKCE;
	}

	@Override
	public void init(String session, String challange) {
		this.cacheRepository.storeObject(this.store, session, challange);
		
	}

	@Override
	public boolean verify(String session, String code) throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		String challange = (String) this.cacheRepository.retriveObject(this.store, session);
		String encryptedCode = Encryption.withSHA3(code);
		if(challange.equals(encryptedCode)) {
			return true;
		}
		return false;
	}

	
	
}
