package com.kabutar.keyfort.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class Jwt {

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

    private long getExpiryTime(long time){
        return time*1000;
    }

    public Claims extractAllClaim(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(this.getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaim(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(
            Map<String,Object> extraClaims,
            String username
    ){
        return this.generateToken(extraClaims,username,EXPIRE);
    }

    public String generateToken(
            Map<String,Object> extraClaims,
            String username,
            long expiryTime
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + this.getExpiryTime(expiryTime)))
                .signWith(this.getSigningKey(),this.getSigningAlgorithm())
                .compact();
    }

    public Date getExpiryTime(String token){
        return this.extractClaim(token,Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        return this.getExpiryTime(token).before(new Date(System.currentTimeMillis()));
    }

    public String extractUsername(String token){
        return this.extractClaim(token,Claims::getSubject);
    }

}
