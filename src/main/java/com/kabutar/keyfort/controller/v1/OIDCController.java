package com.kabutar.keyfort.controller.v1;

import com.kabutar.keyfort.constant.OIDCConstant;
import com.kabutar.keyfort.http.ResponseFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/oidc/{dimension}")
public class OIDCController {
    private final Logger logger = LogManager.getLogger(OIDCController.class);

    @GetMapping("/.well-known/openid-configuration")
    public Mono<ResponseEntity<?>> discoveryEndpoint(@PathVariable("dimension") String dimension, ServerHttpRequest request){
        Map<String, Object> data = new HashMap<>();

        StringBuilder issuesBuilder = new StringBuilder();
        issuesBuilder.append(request.getURI().getScheme());
        issuesBuilder.append("://");
        issuesBuilder.append(request.getURI().getHost());

        if(request.getURI().getPort() != 80 && request.getURI().getPort() != 443){
            issuesBuilder.append(":");
            issuesBuilder.append(request.getURI().getPort());
        }

        String baseUrl = issuesBuilder.toString();
        data.put(OIDCConstant.ISSUER,baseUrl);
        data.put(OIDCConstant.AUTHORIZATION_ENDPOINT,baseUrl + "/api/v1/auth/" + dimension + "/authorize");
        data.put(OIDCConstant.TOKEN_ENDPOINT, baseUrl + "/api/v1/auth/" + dimension + "/token");
        data.put(OIDCConstant.EXCHANGE_TOKEN_ENDPOINT, baseUrl + "/api/v1/auth/" + dimension + "/exchange_token");
        data.put(OIDCConstant.USERINFO_ENDPOINT, baseUrl + "/api/v1/auth/" + dimension + "/userinfo");
        data.put(OIDCConstant.JWKS_URI,baseUrl + "/api/v1/oidc/" + dimension + "/.well-known/jwks.json");
        data.put(OIDCConstant.SCOPES_SUPPORTED,List.of("openid","profile","email","address","phone"));
        data.put(OIDCConstant.RESPONSE_TYPES_SUPPORTED, List.of("code", "token", "id_token", "code token", "code id_token"));
        data.put(OIDCConstant.SUBJECT_TYPES_SUPPORTED, List.of("public","pairwise"));
        data.put(OIDCConstant.ID_TOKEN_SIGNING_ALG_VALUES_SUPPORTED, List.of("RSA256, SHA3,SHA256"));
        data.put(OIDCConstant.TOKEN_ENDPOINT_AUTH_METHODS_SUPPORTED, List.of("client_secret_basic", "client_secret_post"));

        return ResponseFactory.builder().send(data);
    }

    @GetMapping("/.well-known/jwks.json")
    public Mono<ResponseEntity<?>> jwksEndpoint(@PathVariable("dimension") String dimension, ServerHttpRequest request){
        // TODO: Implement JWKS.json endpoint

        return new ResponseFactory().status(HttpStatus.OK).build();
    }
}
