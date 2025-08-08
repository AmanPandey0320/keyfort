package com.kabutar.keyfort.security.service;

import org.springframework.stereotype.Service;

import com.kabutar.keyfort.data.entity.DepUser;

import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RoleService {

    public Mono<List<String>> getRolesForUser(DepUser user){
        return Mono.just(List.of("default"));
    }
}
