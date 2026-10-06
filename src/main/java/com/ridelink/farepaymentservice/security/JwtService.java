package com.ridelink.farepaymentservice.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Service responsible for validating JWT tokens and extracting user claims.
 * Uses the shared secret key matching Account Service.
 */
@Service
public class JwtService {

    private final Key signingKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * Extracts user email (subject) from JWT.
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extracts user ID from JWT claims.
     */
    public String extractUserId(String token) {
        Object userId = extractClaims(token).get("userId");
        return userId != null ? userId.toString() : null;
    }

    /**
     * Extracts user role from JWT claims.
     */
    public String extractRole(String token) {
        Object role = extractClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    /**
     * Validates the token signature and expiration.
     */
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Parses and returns all JWT claims.
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiration() {
        return expiration;
    }
}
