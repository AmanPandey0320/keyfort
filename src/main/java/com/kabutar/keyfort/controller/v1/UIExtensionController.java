package com.kabutar.keyfort.controller.v1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kabutar.keyfort.data.entity.DimensionEntity;
import com.kabutar.keyfort.data.repository.DimensionRepo;
import com.kabutar.keyfort.http.ResponseFactory;
import com.kabutar.keyfort.ui.service.UIExtService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ui-ext/{dimension}")
public class UIExtensionController {
	
	@Autowired
	private UIExtService uiService;
	
	@Autowired
	private DimensionRepo repo;
	
	@GetMapping("/home")
	public Mono<ResponseEntity<?>> getConsoleData(@PathVariable("dimension") String dimension){
		
		uiService.execute();
		
		Flux<DimensionEntity> dimensions = repo.findAll();
		
		Mono<List<DimensionEntity>> dimensionsListMono = dimensions.collectList();

	    
	    return dimensionsListMono.flatMap(dimensionList -> {
	        
	        dimensionList.forEach(item -> System.out.println(item.toString()));

	        return new ResponseFactory()
	                .data(List.of(dimensionList))
	                .status(HttpStatus.OK)
	                .build();
	    });
	}
}
