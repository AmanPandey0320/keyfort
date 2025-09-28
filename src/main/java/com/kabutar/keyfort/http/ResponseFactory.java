package com.kabutar.keyfort.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResponseFactory {

    private HttpStatus status;
    private List<String> error;
    private List<String> message;
    private List<Object> data;
    private HttpHeaders httpHeaders;

    public ResponseFactory() {
        this.error = Collections.emptyList();
        this.message = Collections.emptyList();
        this.data = Collections.emptyList();
        this.status = HttpStatus.OK;
        this.httpHeaders = new HttpHeaders();
    }

    public static ResponseFactory builder(){
        return new ResponseFactory();
    }

    public ResponseFactory status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public ResponseFactory error(List<String> error) {
        this.error = error;
        return this;
    }

    public ResponseFactory message(List<String> message) {
        this.message = message;
        return this;
    }

    public ResponseFactory data(List<Object> data) {
        this.data = data;
        return this;
    }

    public ResponseFactory cookie(ResponseCookie cookie) {
        this.httpHeaders.add(HttpHeaders.SET_COOKIE, this.cookieToString(cookie));
        return this;
    }

    public ResponseFactory redirect(String uri) {
        this.httpHeaders.add("Location", uri);

        return this;
    }

    public Mono<ResponseEntity<?>> build() {
        return Mono.just(ResponseEntity.status(status)
                .headers(httpHeaders)
                .body(Map.of("error", error, "message", message, "data", data)));
    }

    public Mono<ResponseEntity<?>> send(Object object){
        return Mono.just(ResponseEntity.status(this.status).body(object));
    }

    private String cookieToString(ResponseCookie cookie) {
        StringBuilder builder = new StringBuilder();

        // add cookie value
        builder.append(cookie.getName() + "=" + cookie.getValue());

        // add Max-Age
        builder.append("; Max-Age=" + Long.toString(cookie.getMaxAge().getSeconds()));

        // add Path
        builder.append("; Path=/;");

        // add secure
        if (cookie.isSecure()) {
            builder.append("; Secure");
        }

        // add Http-only
        if (cookie.isHttpOnly()) {
            builder.append("; HttpOnly");
        }

        builder.append("; SameSite=strict");

        return builder.toString();
    }
}
