package com.kabutar.keyfort.repository;

import com.kabutar.keyfort.Entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    public User findByUsername (String username);
    public User findByEmail (String email);
    public User findById (String id);
}
