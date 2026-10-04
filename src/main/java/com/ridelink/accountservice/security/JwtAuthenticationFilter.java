package com.ridelink.accountservice.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import com.ridelink.accountservice.model.User;
import com.ridelink.accountservice.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {

        this.jwtService = jwtService;

        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        /*
         * No Authorization header.
         *
         * We simply continue.
         * Spring Security will later reject protected endpoints.
         */
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;
        }

        /*
         * Remove "Bearer " from the beginning.
         */
        String token = authorizationHeader.substring(7);

        try {

            /*
             * Validate JWT.
             */
            if (!jwtService.isTokenValid(token)) {

                filterChain.doFilter(request, response);

                return;
            }

            /*
             * Get email from JWT.
             */
            String email = jwtService.extractEmail(token);

            /*
             * Find current user from MongoDB.
             *
             * This also means a suspended/deactivated user
             * can be blocked immediately.
             */
            User user = userRepository
                    .findByEmail(email)
                    .orElse(null);

            if (user == null) {

                filterChain.doFilter(request, response);

                return;
            }

            /*
             * Only ACTIVE users are allowed to continue.
             */
            if (!user.getStatus().name().equals("ACTIVE")) {

                filterChain.doFilter(request, response);

                return;
            }

            /*
             * Convert our application role into
             * Spring Security's role format.
             *
             * ADMIN becomes ROLE_ADMIN.
             */
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                    "ROLE_" + user.getRole().name());

            /*
             * Create authenticated user information.
             */
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    java.util.List.of(authority));

            /*
             * Tell Spring Security that this request
             * belongs to this authenticated user.
             */
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (Exception exception) {

            /*
             * Invalid token.
             *
             * We don't crash the server.
             * Spring Security will reject protected APIs.
             */
        }

        filterChain.doFilter(request, response);
    }
}