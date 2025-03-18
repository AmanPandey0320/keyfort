package com.kabutar.keyfort.service;

import com.kabutar.keyfort.Entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    public List<String> getRolesForUser(User user){
        return List.of("default");
    }
}
