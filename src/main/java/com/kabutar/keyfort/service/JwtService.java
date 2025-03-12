package com.kabutar.keyfort.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtService {

    @Value("${security.jwt.secret}")
    private String SECRET;

    @Value("${security.jwt.expire}")
    private long EXPIRE;

    @Value("${security.jwt.algorithm}")
    private String ALGORITHM;

    private SignatureAlgorithm getSigningAlgorithm(){
        return SignatureAlgorithm.forName(ALGORITHM);
    }

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private long getExpiryTime(){
        return EXPIRE*1000;
    }

    public String generateToken(
            Map<String,Object> extraClaims,
            String username
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + this.getExpiryTime()))
                .signWith(this.getSigningKey(),this.getSigningAlgorithm())
                .compact();
    }




}
