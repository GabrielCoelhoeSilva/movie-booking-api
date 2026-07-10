package com.gabriel.moviebooking.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> sensitiveBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    private Bucket createLoginBucket() {
        Bandwidth limit = Bandwidth.classic(
                5,
                Refill.greedy(5, Duration.ofMinutes(1))
        );
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createSensitiveBucket() {
        Bandwidth limit = Bandwidth.classic(
                3,
                Refill.greedy(3, Duration.ofHours(1))
        );
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createGeneralBucket() {
        Bandwidth limit = Bandwidth.classic(
                60,
                Refill.greedy(60, Duration.ofMinutes(1))
        );
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        String ip = request.getRemoteAddr();

        Bucket bucket;

        if (path.equals("/api/v1/auth/login") && method.equals("POST")) {
            bucket = loginBuckets.computeIfAbsent(ip, k -> createLoginBucket());

        } else if ((path.equals("/api/v1/auth/register") ||
                path.equals("/api/v1/auth/forgot-password") ||
                path.equals("/api/v1/auth/resend-code")) && method.equals("POST")) {
            bucket = sensitiveBuckets.computeIfAbsent(ip, k -> createSensitiveBucket());

        } else {
            bucket = generalBuckets.computeIfAbsent(ip, k -> createGeneralBucket());
        }

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                        "status": 429,
                        "error": "Too Many Requests",
                        "message": "You have exceeded the request limit. Try again later."
                    }
                    """);
        }
    }
}