package com.quest.questdemo.config.authenticationPrePosthandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Extract user details from OAuth2User
        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            String email = (String) oAuth2User.getAttributes().get("email");
            String name = (String) oAuth2User.getAttributes().get("name");
            // --- Custom verification logic here ---
            boolean isVerified = true; // Replace with your verification logic
            if (isVerified) {
                System.out.println("*** OAuth2 authentication success for: " + email + " (" + name + ") ***");
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                System.out.println("*** OAuth2 authentication verification failed for: " + email + " (" + name + ") ***");
                // Clear authentication and invalidate session
                SecurityContextHolder.clearContext();
                if (request.getSession(false) != null) {
                    request.getSession(false).invalidate();
                }
                response.sendRedirect(request.getContextPath() + "/LoginPage?error=verification_failed");
            }
        } else {
            // Not an OAuth2User, fallback
            response.sendRedirect(request.getContextPath() + "/LoginPage?error=invalid_auth");
        }
    }
}