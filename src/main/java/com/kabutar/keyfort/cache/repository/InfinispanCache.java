package com.kabutar.keyfort.cache.repository;

import com.kabutar.keyfort.cache.intefaces.CacheRepository;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

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
    public Mono<Void> storeObject(String store, Object key, Object value) {
        this.cacheManager.getCache(store).putAsync(key, value);
        return Mono.empty();
    }

    @Override
    public Mono<?> retriveObject(String store, String key) {
        Cache<String, String> cache = cacheManager.getCache(store);
        Mono<String> data = Mono.fromCompletionStage(() -> cache.getAsync(key));
        if (this.deleteOnRetrive) {
            cache.removeAsync(key);
        }
        return data;
    }
}
