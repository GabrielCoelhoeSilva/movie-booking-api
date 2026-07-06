package com.gabriel.moviebooking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull; // Importação necessária para tirar o aviso
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserLoggingFilter extends OncePerRequestFilter {

    private static final String USER_KEY = "username";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,       // Adicionado @NonNull
            @NonNull HttpServletResponse response,     // Adicionado @NonNull
            @NonNull FilterChain filterChain           // Adicionado @NonNull
    ) throws ServletException, IOException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()
                    && !authentication.getPrincipal().equals("anonymousUser")) {
                MDC.put(USER_KEY, authentication.getName());
            } else {
                MDC.put(USER_KEY, "anonymous");
            }

            filterChain.doFilter(request, response);

        } finally {
            MDC.remove(USER_KEY);
        }
    }
}