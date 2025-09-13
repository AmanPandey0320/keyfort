package com.kabutar.keyfort.security.service;

import org.springframework.stereotype.Service;

import com.kabutar.keyfort.data.entity.User;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
public class RoleService {

    public Mono<List<String>> getRolesForUser(UUID userId){
        return Mono.just(List.of("default"));
    }
}
