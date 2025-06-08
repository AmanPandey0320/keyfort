package com.kabutar.keyfort.cache.services;

import org.springframework.stereotype.Component;

import com.kabutar.keyfort.cache.intefaces.InMemoryCache;

@Component
public class LocalCache implements InMemoryCache {

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeObject(Object object, Object key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void retriveObject(Object key) {
		// TODO Auto-generated method stub

	}

}
