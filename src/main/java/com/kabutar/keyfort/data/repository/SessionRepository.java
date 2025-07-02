package com.kabutar.keyfort.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kabutar.keyfort.data.entity.Session;

public interface SessionRepository extends JpaRepository<Session,String> {
	@Query(value = "SELECT * FROM session as s WHERE s.id = :id",
            nativeQuery = true)
	public Optional<Session> findById(@Param("id") String id);
	
}
