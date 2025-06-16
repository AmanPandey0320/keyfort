package com.kabutar.keyfort.cache.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.kabutar.keyfort.cache.intefaces.CacheRepository;

@Component
@ConditionalOnProperty(name = "cache.type", havingValue = "REDIS")
public class DistributedCache implements CacheRepository {

	@Override
	public void storeObject(String store, Object object, Object key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object retriveObject(String store, Object key) {
		// TODO Auto-generated method stub
		return null;
	}

}
