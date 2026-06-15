package com.cybercafe.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
    @Value("auth.secret")
    private String secret;

    private SecretKey getSigningKey() {
        byte[] bytes = this.secret.getBytes();
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(String account) {
        
        return Jwts.builder()
                .subject(account)
                .issuedAt(new Date())
                .expiration(
                    new Date(System.currentTimeMillis() + 86400000)
                )
                .signWith(getSigningKey())
                .compact();

    }

    public String extractAccount(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean isValid(String token) {
        try {

            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

}
