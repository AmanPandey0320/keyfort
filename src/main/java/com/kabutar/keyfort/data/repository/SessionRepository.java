package com.kabutar.keyfort.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kabutar.keyfort.data.entity.Session;

public interface SessionRepository extends JpaRepository<Session,String> {
	
}
