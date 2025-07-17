package com.quest.questdemo.jwt;

import com.quest.questdemo.config.authproviders.CustomDBAuthenticationProvider;
import com.quest.questdemo.config.authproviders.CustomLDAPAuthenticationProvider;
import com.quest.questdemo.jwt.dto.LoginRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/jwttlogin")
public class JwtLoginController {

    @Autowired
    private CustomLDAPAuthenticationProvider customLDAPAuthenticationProvider;

    @Autowired
    private CustomDBAuthenticationProvider customDBAuthenticationProvider;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody LoginRequestDto loginRequest) {
        try {
            // Create authentication token
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

            // Try authentication using LDAP first, then DB
            Authentication authentication = null;
            String authType = "";
            
            try {
                authentication = customLDAPAuthenticationProvider.authenticate(authToken);
                authType = "LDAP";
            } catch (AuthenticationException e) {
                // LDAP failed, try DB authentication
                authentication = customDBAuthenticationProvider.authenticate(authToken);
                authType = "DATABASE";
            }

            if (authentication != null && authentication.isAuthenticated()) {
                // Generate actual JWT token using JwtUtil
                String jwtToken = jwtUtil.generateToken(authentication);
                
                Map<String, Object> response = new HashMap<>();
                response.put("token", jwtToken);
                response.put("username", authentication.getName());
                response.put("authorities", authentication.getAuthorities());
                response.put("authType", authType);
                response.put("message", "Authentication successful");
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Authentication failed"));
            }
            
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Authentication error: " + e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "JWT Login endpoint is active");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Authorization header (remove "Bearer " prefix)
            String token = authHeader.replace("Bearer ", "");
            
            // Validate token using JwtUtil
            if (jwtUtil.validateToken(token)) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", jwtUtil.getUsernameFromToken(token));
                response.put("authorities", jwtUtil.getAuthoritiesFromToken(token));
                response.put("authType", jwtUtil.getAuthTypeFromToken(token));
                response.put("expiresAt", jwtUtil.getExpirationDateFromToken(token));
                response.put("message", "Token is valid");
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid or expired token"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Invalid Authorization header format"));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", true);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}
