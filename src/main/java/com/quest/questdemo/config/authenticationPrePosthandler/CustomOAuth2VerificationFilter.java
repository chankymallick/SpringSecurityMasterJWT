package com.quest.questdemo.config.authenticationPrePosthandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CustomOAuth2VerificationFilter extends OncePerRequestFilter {
    private final AntPathRequestMatcher matcher = new AntPathRequestMatcher("/login/oauth2/code/*");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (matcher.matches(request)) {
            // --- Custom verification logic here ---
            // Example: check a request parameter or session attribute
            String verificationParam = request.getParameter("verify");
            boolean isVerified = true;
            if (isVerified) {
                System.out.println("*** OAuth2 verification successful ***");
                // Verification successful, continue authentication
                filterChain.doFilter(request, response);
                return;
            } else {
                System.out.printf("*** OAuth2 verification failed ***\n");
                // Verification failed, redirect to login page with error
                response.sendRedirect(request.getContextPath() + "/LoginPage?error=verification_failed");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
