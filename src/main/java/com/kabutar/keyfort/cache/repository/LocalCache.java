package com.kabutar.keyfort.cache.repository;

import com.kabutar.keyfort.cache.intefaces.CacheRepository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "cache.type", havingValue = "LOCAL")
public class LocalCache implements CacheRepository {

    private Map<Object, Object> cache;

    public LocalCache() {
        super();
        this.cache = new HashMap<>();
    }

    @Override
    public Mono<Void> storeObject(String store, Object key, Object value) {
        this.cache.put(key, value);
        return Mono.empty();
    }

    @Override
    public Mono<?> retriveObject(String store, String key) {
        if (this.cache.containsKey(key)) {
            return Mono.just(this.cache.get(key));
        }
        return null;
    }
}
