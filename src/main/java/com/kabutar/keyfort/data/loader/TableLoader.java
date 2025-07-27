package com.kabutar.keyfort.data.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kabutar.keyfort.data.repository.ClientRepo;
import com.kabutar.keyfort.data.repository.DimensionRepo;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Component
public class TableLoader implements DefaultLoader {
	
	@Autowired
	private DimensionRepo dimensionRepo;
	
	@Autowired
	private ClientRepo clientRepo;
	
	private void createTables() throws Exception{
		this.dimensionRepo.create();
		this.clientRepo.create();
		
		
	}

	@Override
	@PostConstruct
	public void loadData() {
		try {
			this.createTables();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}

}
