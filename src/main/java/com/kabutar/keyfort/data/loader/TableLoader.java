package com.kabutar.keyfort.data.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kabutar.keyfort.data.repository.DimensionRepo;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Component
public class TableLoader implements DefaultLoader {
	
	@Autowired
	private DimensionRepo dimensionRepo;
	
	private Mono<Void> createTables() throws Exception{
		dimensionRepo.create().then().subscribe();
		
		return Mono.empty();
		
	}

	@Override
	@PostConstruct
	public void loadData() {
		try {
			this.createTables().subscribe();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}

}
