package com.kabutar.keyfort.cache.repository;

import com.kabutar.keyfort.cache.intefaces.CacheRepository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(name = "cache.type", havingValue = "REDIS")
public class DistributedCache implements CacheRepository {

    @Override
    public Mono<Void> storeObject(String store, Object object, Object key) {
        // TODO Auto-generated method stub
        return Mono.empty();
    }

    @Override
    public Mono<?> retriveObject(String store, String key) {
        // TODO Auto-generated method stub
        return null;
    }
}
