package com.kabutar.keyfort.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import com.kabutar.keyfort.config.AuthConfig;
import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.service.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthrizationFilter extends OncePerRequestFilter {
	
	private AuthConfig authConfig;
	private AuthService authService;
	private PathPatternParser parser;
	private final List<PathPattern> patterns;
	private final int patternSize;
	
	
	
	@Autowired
	public AuthrizationFilter(AuthConfig authConfig, AuthService authService) {
		super();
		this.authConfig = authConfig;
		this.authService = authService;
		this.parser = new PathPatternParser();
		this.patterns = this.authConfig.getPreAuthnUrls().stream().map((AuthConfig.PreAuthnUrl url) -> parser.parse(url.getPath())).toList();
		this.patternSize = this.patterns.size();
	}

	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
			throws ServletException, IOException {
		Map<String,Cookie> cookies = List.of(((request.getCookies() == null)? new Cookie[0]:request.getCookies())).stream().collect(Collectors.toMap(Cookie::getName, c -> c));
		String forwardHost = request.getHeader("X-Forwarded-Host");
		String forwardProtocol = request.getHeader("X-Forwarded-Proto");
		if(!(cookies.containsKey(AuthConstant.CookieType.ACCESS_TOKEN) && cookies.containsKey(AuthConstant.CookieType.REFRESH_TOKEN))) {
			response.sendRedirect(forwardProtocol+"://"+forwardHost+"/console/auth/signin");
			return;
		}
		
		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();
		String method = request.getMethod();
		
//		for(int i=0;i<this.patternSize;i++) {
//			if(this.patterns.get(i).matches(PathContainer.parsePath(path))
//					&& this.authConfig.getPreAuthnUrls().get(i).getMethod().equals(method)
//					) {
//				System.out.println("Its a match");
//				return true;
//			}
//		}
		return false;
	}
	
	

}
