package com.kabutar.keyfort.filter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.data.entity.Session;
import com.kabutar.keyfort.data.repository.SessionRepository;
import com.kabutar.keyfort.http.SessionInjectedHttpRequest;

import reactor.core.publisher.Mono;


@Component
public class SessionFilter implements WebFilter  {
	
	private SessionRepository sessionRepository;
	
	

	public SessionFilter(SessionRepository sessionRepository) {
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
		if(cookie != null) {
			return chain.filter(exchange);
		}
		
		// Create new session
		Session session = new Session();
		session.setAuthenticated(false);
        session.setCreatedAt(new Date(System.currentTimeMillis()));
        session.setLastUsed(new Date(System.currentTimeMillis()));
		
        sessionRepository.save(session);
		
        ResponseCookie responseCookie = ResponseCookie.from(AuthConstant.CookieType.SESSION_ID, session.getId())
                .httpOnly(true)
                .path("/")
                .maxAge(AuthConstant.ExpiryTime.SESSION)
                .build();

        // Add the cookie to the response
        exchange.getResponse().addCookie(responseCookie);
        
        exchange.getAttributes().put(AuthConstant.CookieType.SESSION_ID, responseCookie);
		
		return chain.filter(exchange);
		
	}



//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		Cookie[] cookies = request.getCookies();
//		boolean isSessionPresent = false;
//		
//		if(cookies != null) {
//			for(Cookie c: cookies) {
//				if(c.getName().equals(AuthConstant.CookieType.SESSION_ID)) {
//					isSessionPresent = true;
//				}
//			}
//		}
//		
//		if(!isSessionPresent) {
//			Session session = new Session();
//			session.setAuthenticated(false);
//	        session.setCreatedAt(new Date(System.currentTimeMillis()));
//	        session.setLastUsed(new Date(System.currentTimeMillis()));
//			
//	        sessionRepository.save(session);
//	        
//	        Cookie sessionCookie = new Cookie(AuthConstant.CookieType.SESSION_ID, session.getId());
//            sessionCookie.setHttpOnly(true);
//            sessionCookie.setPath("/");
//            sessionCookie.setMaxAge(AuthConstant.ExpiryTime.SESSION);
//            
//            response.addCookie(sessionCookie);
//            
//            request = new SessionInjectedHttpRequest(request,sessionCookie);
//		}
//		
//		filterChain.doFilter(request, response);
//		
//	}

}
