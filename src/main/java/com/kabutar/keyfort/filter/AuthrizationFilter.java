package com.kabutar.keyfort.filter;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kabutar.keyfort.config.AuthConfig;
import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.security.service.AuthService;

import reactor.core.publisher.Mono;


@Component
public class AuthrizationFilter implements WebFilter  {
	
	private final Logger logger  = LogManager.getLogger(AuthrizationFilter.class);
	
	private AuthConfig authConfig;
	private AuthService authService;
	private PathPatternParser parser;
	private final List<PathPattern> patterns;
	private final int patternSize;
	private final ObjectMapper objectMapper;
	
	
	
	public AuthrizationFilter(AuthConfig authConfig, AuthService authService) {
		super();
		this.authConfig = authConfig;
		this.authService = authService;
		this.parser = new PathPatternParser();
		this.patterns = this.authConfig.getPreAuthnUrls().stream().map((AuthConfig.PreAuthnUrl url) -> parser.parse(url.getPath())).toList();
		this.patternSize = this.patterns.size();
		this.objectMapper = new ObjectMapper();
	}



	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		
//		if(this.shouldNotFilter(exchange.getRequest())) {
//			return chain.filter(exchange);
//		}
//		
//		ServerHttpResponse response = exchange.getResponse();
//		HttpCookie accessTokenCookie = exchange
//				.getRequest()
//				.getCookies()
//				.getOrDefault(AuthConstant.CookieType.ACCESS_TOKEN, List.of())
//				.stream()
//				.findFirst()
//				.orElse(null);
//		
//		if(accessTokenCookie != null) {
//			return authService.validateAccessToken(accessTokenCookie.getValue()).flatMap(isValid -> {
//				if(isValid) {
//					return chain.filter(exchange);
//				}
//				
//				return this.sendErrorResponse(response);
//				
//			});
//		}
//		return this.sendErrorResponse(response);
		
		// TODO: remove this line
		return chain.filter(exchange);
	}
	
	/**
	 * 
	 * @param response
	 * @return
	 */
	private Mono<Void> sendErrorResponse(ServerHttpResponse response) {
		try {
			String responseBody = this.objectMapper.writeValueAsString(Map.of("error", List.of("Unauthorized")));
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			
			logger.error("Error at authorization filter: unauthorized access");
			
			return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8))));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Error in authorization filter: {}",e.getLocalizedMessage());
			logger.debug(e);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			
			return response.writeWith(Mono.empty());
		}
		
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	private boolean shouldNotFilter( ServerHttpRequest request ) {
		String path = request.getPath().toString();
		String method = request.getMethod().name();
		
		logger.info("request path: {}, request method: {}",path,method);
		
		for(int i=0;i<this.patternSize;i++) {
			if(this.patterns.get(i).matches(PathContainer.parsePath(path))
					&& this.authConfig.getPreAuthnUrls().get(i).getMethod().equals(method)
					) {
				return true;
			}
		}
		return false;
	}	
	

}
