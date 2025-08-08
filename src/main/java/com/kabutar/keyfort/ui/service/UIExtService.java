package com.kabutar.keyfort.ui.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kabutar.keyfort.data.entity.Dimension;
import com.kabutar.keyfort.data.repository.DimensionRepo;
import com.kabutar.keyfort.security.service.AuthService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UIExtService {
	
	private final Logger logger = LogManager.getLogger(AuthService.class);
	
	@Autowired
	private DimensionRepo repo;
	
	public Mono<Void> execute(){
		
		
		return Mono.empty();
	}

}
