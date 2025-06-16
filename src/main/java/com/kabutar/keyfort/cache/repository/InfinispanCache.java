package com.kabutar.keyfort.cache.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import com.kabutar.keyfort.cache.intefaces.CacheRepository;
import com.kabutar.keyfort.cache.metadata.PkceMetadata;

@Component
@ConditionalOnProperty(name = "cache.type", havingValue = "INF-EMB")
public class InfinispanCache implements CacheRepository {
	
	@Value("${cache.deleteOnRetrieve}")
	private boolean deleteOnRetrive;
	
	private EmbeddedCacheManager cacheManager;
	
	

	public InfinispanCache(EmbeddedCacheManager cacheManager) {
		super();
		this.cacheManager = cacheManager;
	}



	@Override
	public void storeObject(String store, Object key, Object value) {
		this.cacheManager.getCache(store).put(key, value);
	}



	@Override
	public Object retriveObject(String store, Object key) {
		Cache<String,String> cache = cacheManager.getCache(store);
		String data = cache.get(key);
		if(this.deleteOnRetrive) {
			cache.remove(key);
		}
		return data;
	}

}
