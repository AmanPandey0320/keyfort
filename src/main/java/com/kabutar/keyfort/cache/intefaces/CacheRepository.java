package com.kabutar.keyfort.cache.intefaces;

public interface CacheRepository {
	void storeObject (String store, Object key, Object value);
	Object retriveObject(String store, Object key);
}
