package com.quest.questdemo.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter for validating JWT tokens in API requests
 * This filter intercepts requests to /api/** endpoints and validates JWT tokens
 * If token is valid, it sets the authentication in SecurityContext
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Filter method that processes each request to validate JWT tokens
     * @param request - HTTP request
     * @param response - HTTP response  
     * @param filterChain - Filter chain to continue processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // Get Authorization header from request
        String authorizationHeader = request.getHeader("Authorization");
        
        // Check if Authorization header exists and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            
            // Extract JWT token by removing "Bearer " prefix
            String jwtToken = authorizationHeader.substring(7);
            
            try {
                // Validate the JWT token
                if (jwtUtil.validateToken(jwtToken)) {
                    
                    // Extract user information from token
                    String username = jwtUtil.getUsernameFromToken(jwtToken);
                    List<String> authorities = jwtUtil.getAuthoritiesFromToken(jwtToken);
                    String authType = jwtUtil.getAuthTypeFromToken(jwtToken);
                    
                    // Convert string authorities to GrantedAuthority objects
                    List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                    
                    // Create authentication object
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
                    
                    // Set additional details about authentication type
                    authentication.setDetails(authType);
                    
                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    System.out.println("JWT Authentication successful for user: " + username + " (Auth Type: " + authType + ")");
                } else {
                    // Token is invalid
                    System.err.println("Invalid JWT token in request");
                }
                
            } catch (Exception e) {
                // Token parsing failed
                System.err.println("JWT token parsing failed: " + e.getMessage());
            }
        }
        
        // Continue with the filter chain regardless of authentication status
        // If no valid token, the request will be handled by security configuration
        filterChain.doFilter(request, response);
    }

    /**
     * Determines if this filter should be applied to the current request
     * Only apply to /api/** endpoints
     * @param request - HTTP request
     * @return true if filter should be applied, false otherwise
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();  // Use getServletPath() instead of getRequestURI()
        // Only filter /api/** paths, skip others
        return !path.startsWith("/api/");
    }
}
