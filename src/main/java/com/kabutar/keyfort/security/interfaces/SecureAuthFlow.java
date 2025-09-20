package com.kabutar.keyfort.security.interfaces;

import reactor.core.publisher.Mono;

public interface SecureAuthFlow {
    Mono<Void> init(String session, String challange);

    Mono<Boolean> verify(String session, String code);
}
