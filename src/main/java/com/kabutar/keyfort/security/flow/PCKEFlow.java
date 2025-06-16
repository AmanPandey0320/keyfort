package com.kabutar.keyfort.security.flow;

import org.springframework.stereotype.Component;

import com.kabutar.keyfort.cache.intefaces.CacheRepository;
import com.kabutar.keyfort.constant.CacheConstant;
import com.kabutar.keyfort.security.interfaces.SecureAuthFlow;

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
	public boolean verify(String session, String code) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
