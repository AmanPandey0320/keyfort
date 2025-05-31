package com.kabutar.keyfort.service;

import org.springframework.stereotype.Service;

import com.kabutar.keyfort.data.entity.User;

import java.util.List;

@Service
public class RoleService {

    public List<String> getRolesForUser(User user){
        return List.of("default");
    }
}
