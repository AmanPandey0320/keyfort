package com.kabutar.keyfort.filter;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.data.entity.Session;
import com.kabutar.keyfort.data.repository.SessionRepo;

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

    private SessionRepo sessionRepository;

    public SessionFilter(SessionRepo sessionRepository) {
        this.sessionRepository = sessionRepository;
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

        // Create new session
        Session session = new Session();
        session.setAuthenticated(false);
        session.setLastUsed(LocalDateTime.now());
        session.setUserId(null);

        try {
            return sessionRepository.save(session).flatMap(s -> {
                ResponseCookie responseCookie = ResponseCookie.from(
                                AuthConstant.CookieType.SESSION_ID, String.valueOf(s.getId()))
                        .httpOnly(true)
                        .path("/")
                        .maxAge(AuthConstant.ExpiryTime.SESSION)
                        .build();
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
