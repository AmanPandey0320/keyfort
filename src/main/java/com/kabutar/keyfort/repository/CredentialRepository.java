package com.kabutar.keyfort.repository;

import com.kabutar.keyfort.Entity.Credential;
import com.kabutar.keyfort.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CredentialRepository extends JpaRepository<Credential,String> {

    @Query(value = "SELECT c FROM credential c WHERE c.user_id = :userId AND c.is_active = true",nativeQuery = true)
    List<Credential> findActiveCredentialsForUser(@Param("user") String userId);

    Credential save(Credential credential);

    @Modifying
    @Transactional
    @Query(value = "UPDATE credential c SET c.is_active = false WHERE c.user_id = :userId", nativeQuery = true)
    void setAllUserCredentialsInactive(String userId);
}
