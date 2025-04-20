package com.kabutar.keyfort.repository;

import com.kabutar.keyfort.Entity.User;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    public User findByUsername (String username);
    public User findByEmail (String email);
    public Optional<User> findById (String id);
}
