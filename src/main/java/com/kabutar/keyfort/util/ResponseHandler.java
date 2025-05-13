package com.kabutar.keyfort.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.kabutar.keyfort.http.Cookie;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResponseHandler {

    private HttpStatus status;
    private List<String> error;
    private List<String> message;
    private List<Object> data;
    private HttpHeaders httpHeaders;
    
    public ResponseHandler() {
    	this.error = Collections.emptyList();
    	this.message = Collections.emptyList();
    	this.data = Collections.emptyList();
    	this.status = HttpStatus.OK;
    	this.httpHeaders = new HttpHeaders();
    }

    public ResponseHandler status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public ResponseHandler error(List<String> error) {
        this.error = error;
        return this;
    }

    public ResponseHandler message(List<String> message) {
        this.message = message;
        return this;
    }

    public ResponseHandler data(List<Object> data) {
        this.data = data;
        return this;
    }
    
    public ResponseHandler cookie(Cookie cookie) {
    	this.httpHeaders.add("Set-Cookie",cookie.getCookie());
    	return this;
    }
    
    public ResponseHandler redirect(String uri) {
    	this.httpHeaders.add("Location", uri);
    	
    	return this;
    }

    public ResponseEntity<?> build() {
        return ResponseEntity
        		.status(status)
        		.headers(httpHeaders)
        		.body(
        				Map.of(
        						"error", error,
        						"message", message,
        						"data", data)
        		);
    }
}
