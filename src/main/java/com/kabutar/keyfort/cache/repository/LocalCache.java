package com.kabutar.keyfort.cache.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.kabutar.keyfort.cache.intefaces.CacheRepository;

@Component
@ConditionalOnProperty(name = "cache.type", havingValue = "LOCAL")
public class LocalCache implements CacheRepository {
	
	private Map<Object,Object> cache;
	
	

	public LocalCache() {
		super();
		this.cache = new HashMap<>();
	}

	@Override
	public void storeObject(String store, Object key, Object value) {
		this.cache.put(key, value);

	}

	@Override
	public Object retriveObject(String store, Object key) {
		if(this.cache.containsKey(key)) {
			return this.cache.get(key);
		}
		return null;
	}

}
