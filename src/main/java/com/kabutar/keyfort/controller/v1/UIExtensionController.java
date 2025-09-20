package com.kabutar.keyfort.controller.v1;

import com.kabutar.keyfort.http.ResponseFactory;
import com.kabutar.keyfort.ui.service.UIExtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/ui-ext/{dimension}")
public class UIExtensionController {

    @Autowired
    private UIExtService uiService;

    @GetMapping("/home")
    public Mono<ResponseEntity<?>> getConsoleData(@PathVariable("dimension") String dimension) {
        return uiService.execute().flatMap(dimensionList -> {
            dimensionList.forEach(item -> System.out.println(item.toString()));
            return new ResponseFactory().status(HttpStatus.OK).build();
        });
    }
}
