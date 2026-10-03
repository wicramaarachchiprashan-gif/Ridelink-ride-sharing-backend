package com.ridelink.accountservice.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ridelink.accountservice.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/*
 * Responsible for creating and reading JWT tokens.
 */
@Service
public class JwtService {

    private final Key signingKey;

    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        /*
         * The secret must be long enough for HMAC SHA-256.
         */
        this.signingKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));

        this.expiration = expiration;
    }

    /*
     * Creates a JWT after successful login.
     */
    public String generateToken(User user) {

        Date now = new Date();

        Date expiry = new Date(
                now.getTime() + expiration);

        return Jwts.builder()

                // Subject identifies the logged-in user.
                .subject(user.getEmail())

                // Store user ID in the token.
                .claim("userId", user.getId())

                // Store role in the token.
                .claim("role", user.getRole().name())

                .issuedAt(now)

                .expiration(expiry)

                .signWith(signingKey)

                .compact();
    }

    /*
     * Extracts the email/username from JWT.
     */
    public String extractEmail(String token) {

        return extractClaims(token)
                .getSubject();
    }

    /*
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

    /*
     * Reads all JWT claims.
     */
    private Claims extractClaims(String token) {

        return Jwts.parser()

                .verifyWith(
                        (javax.crypto.SecretKey) signingKey)

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }

    public long getExpiration() {

        return expiration;
    }
}