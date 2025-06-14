package com.kabutar.keyfort.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.Client;
import com.kabutar.keyfort.data.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    public User findByUsername (String username);
    public User findByEmail (String email);
    public Optional<User> findById (String id);
    public List<User> findByClient(Client c);
}
