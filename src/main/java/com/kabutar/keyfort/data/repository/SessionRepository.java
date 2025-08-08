package com.kabutar.keyfort.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kabutar.keyfort.data.entity.DepSession;

public interface SessionRepository extends JpaRepository<DepSession,String> {
	@Query(value = "SELECT * FROM session as s WHERE s.id = :id",
            nativeQuery = true)
	public Optional<DepSession> findById(@Param("id") String id);
	
}
