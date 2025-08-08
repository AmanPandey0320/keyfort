package com.kabutar.keyfort.data.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kabutar.keyfort.data.entity.DepCredential;
import com.kabutar.keyfort.data.entity.DepUser;

import java.util.List;

@Repository
public interface CredentialRepository extends JpaRepository<DepCredential,String> {

    @Query(value = "SELECT * FROM credential c WHERE c.user_id = :userid AND c.is_active = true",nativeQuery = true)
    List<DepCredential> findActiveCredentialsForUser(@Param("userid") String userId);

    DepCredential save(DepCredential credential);

    @Modifying
    @Transactional
    @Query(value = "UPDATE credential c SET c.is_active = false WHERE c.user_id = :userid", nativeQuery = true)
    void setAllUserCredentialsInactive(@Param("userid") String userId);

    @Query(value = "SELECT c FROM credential c WHERE c.user_id = :userid",nativeQuery = true)
    DepCredential findByUserId(@Param("userid") String userId);
}
