package com.kabutar.keyfort.repository;

import com.kabutar.keyfort.Entity.Credential;
import com.kabutar.keyfort.Entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Credential,Integer> {
    public Token save (Token token);
}
