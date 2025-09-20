package com.kabutar.keyfort.cache.intefaces;

import reactor.core.publisher.Mono;

public interface CacheRepository {
    Mono<Void> storeObject(String store, Object key, Object value);

    Mono<?> retriveObject(String store, String key);
}
