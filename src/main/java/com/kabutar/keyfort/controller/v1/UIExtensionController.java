package com.kabutar.keyfort.controller.v1;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kabutar.keyfort.http.ResponseFactory;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ui-ext/{dimension}")
public class UIExtensionController {
	
	@GetMapping("/home")
	public Mono<ResponseEntity<?>> getConsoleData(@PathVariable("dimension") String dimension){
		return new ResponseFactory()
		.data(List.of("data",dimension))
		.status(HttpStatus.OK)
		.build();
	}
}
