package com.kabutar.keyfort.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kabutar.keyfort.Entity.Session;

public interface SessionRepository extends JpaRepository<Session,String> {
	
}
