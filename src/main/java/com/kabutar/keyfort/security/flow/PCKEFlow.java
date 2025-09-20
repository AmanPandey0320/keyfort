package com.kabutar.keyfort.security.flow;

import com.kabutar.keyfort.cache.intefaces.CacheRepository;
import com.kabutar.keyfort.constant.CacheConstant;
import com.kabutar.keyfort.security.interfaces.SecureAuthFlow;
import com.kabutar.keyfort.util.Encryption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;

@Component
public class PCKEFlow implements SecureAuthFlow {

    private final Logger logger = LogManager.getLogger(PCKEFlow.class);

    private CacheRepository cacheRepository;
    private String store;

    public PCKEFlow(CacheRepository cacheRepository) {
        super();
        this.cacheRepository = cacheRepository;
        this.store = CacheConstant.CacheStore.PKCE;
    }

    @Override
    public Mono<Void> init(String session, String challange) {
        this.cacheRepository.storeObject(this.store, session, challange);
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> verify(String session, String code) {
        return this.cacheRepository
                .retriveObject(this.store, session)
                .flatMap((Object challenge) -> {
                    String encryptedCode;
                    try {
                        encryptedCode = Encryption.withSHA3(code);
                        if (String.valueOf(challenge).equals(encryptedCode)) {
                            return Mono.just(true);
                        }
                        return Mono.just(false);
                    } catch (NoSuchAlgorithmException e) {
                        logger.error("Error occured in PKCE flow: {}", e.getLocalizedMessage());
                        logger.debug(e);
                        return Mono.error(e);
                    }
                });
    }
}
