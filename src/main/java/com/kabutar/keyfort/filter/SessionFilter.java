package com.kabutar.keyfort.filter;

import java.io.IOException;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.data.entity.Session;
import com.kabutar.keyfort.data.repository.SessionRepository;
import com.kabutar.keyfort.http.SessionInjectedHttpRequest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SessionFilter extends OncePerRequestFilter {
	
	private SessionRepository sessionRepository;
	
	

	public SessionFilter(SessionRepository sessionRepository) {
		super();
		this.sessionRepository = sessionRepository;
	}



	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		boolean isSessionPresent = false;
		
		if(cookies != null) {
			for(Cookie c: cookies) {
				if(c.getName().equals(AuthConstant.CookieType.SESSION_ID)) {
					isSessionPresent = true;
				}
			}
		}
		
		if(!isSessionPresent) {
			Session session = new Session();
			session.setAuthenticated(false);
	        session.setCreatedAt(new Date(System.currentTimeMillis()));
	        session.setLastUsed(new Date(System.currentTimeMillis()));
			
	        sessionRepository.save(session);
	        
	        Cookie sessionCookie = new Cookie(AuthConstant.CookieType.SESSION_ID, session.getId());
            sessionCookie.setHttpOnly(true);
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(AuthConstant.ExpiryTime.SESSION);
            
            response.addCookie(sessionCookie);
            
            request = new SessionInjectedHttpRequest(request,sessionCookie);
		}
		
		filterChain.doFilter(request, response);
		
	}

}
