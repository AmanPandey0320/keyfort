package com.kabutar.keyfort.filter;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.data.entity.Session;
import com.kabutar.keyfort.data.repository.SessionRepo;

import com.kabutar.keyfort.security.service.AuthService;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SessionFilter implements WebFilter {

    private AuthService authService;

    public SessionFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpCookie cookie = exchange
                .getRequest()
                .getCookies()
                .getOrDefault(AuthConstant.CookieType.SESSION_ID, List.of())
                .stream()
                .findFirst()
                .orElse(null);
        if (cookie != null) {
            exchange.getAttributes().put(AuthConstant.CookieType.SESSION_ID, cookie.getValue());
            return chain.filter(exchange);
        }

        try {
            return this.authService.initSession().flatMap(responseCookie -> {
                // Add the cookie to the response
                exchange.getResponse().addCookie(responseCookie);
                exchange.getAttributes()
                        .put(AuthConstant.CookieType.SESSION_ID, responseCookie.getValue());
                return chain.filter(exchange);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
