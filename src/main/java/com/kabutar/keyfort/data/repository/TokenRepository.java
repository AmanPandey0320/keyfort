package com.kabutar.keyfort.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.DepCredential;
import com.kabutar.keyfort.data.entity.DepToken;

@Repository
public interface TokenRepository extends JpaRepository<DepCredential,String> {
    public DepToken save (DepToken token);

    @Query(
            value = "SELECT * FROM token as t WHERE t.token = :token",
            nativeQuery = true
    )
    public DepToken findByToken(@Param("token") String token);

}
