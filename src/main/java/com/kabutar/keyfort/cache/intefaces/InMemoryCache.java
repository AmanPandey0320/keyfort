package com.kabutar.keyfort.cache.intefaces;

public interface InMemoryCache {
	void init();
	void storeObject (Object object, Object key);
	void retriveObject(Object key);
}
