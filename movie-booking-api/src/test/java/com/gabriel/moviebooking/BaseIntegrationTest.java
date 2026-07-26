package com.gabriel.moviebooking;

import com.gabriel.moviebooking.security.RateLimitingFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

public abstract class BaseIntegrationTest {

    @MockitoBean
    protected RateLimitingFilter rateLimitingFilter;

    @BeforeEach
    void bypassRateLimit() throws Exception {
        doAnswer(invocation -> {
            HttpServletRequest request   = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain            = invocation.getArgument(2);
            chain.doFilter(request, response); // sempre deixa passar
            return null;
        }).when(rateLimitingFilter).doFilter(any(), any(), any());
    }
}