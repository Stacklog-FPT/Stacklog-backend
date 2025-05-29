package com.stacklog.core_service.utils.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

import org.springframework.stereotype.Component;

@Component
public class JwtDecoder {

    private final JwtProperties jwtProperties;


    public JwtDecoder(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String getIdFromToken(String token) {

        try {
            Key key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            String id = claims.getBody().get("id", String.class);
            // String username = claims.getBody().get("username", String.class);

            return id;

        } catch (JwtException e) {
            System.out.println("❌ Invalid JWT: " + e.getMessage());
        }
        return null;
    }
}
