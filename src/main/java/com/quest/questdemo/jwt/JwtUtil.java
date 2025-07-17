package com.quest.questdemo.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Utility class for generating, parsing, and validating JWT tokens
 * This class handles all JWT operations including token creation, validation, and claims extraction
 */
@Component
public class JwtUtil {
    
    // Secret key for signing JWT tokens (Base64 encoded)
    // In production, this should be stored in environment variables or secure configuration
    private static final String JWT_SECRET = "mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForSecurity";
    
    // Token expiration time in milliseconds (24 hours)
    private static final int JWT_EXPIRATION_MS = 86400000; // 24 hours = 24 * 60 * 60 * 1000
    
    /**
     * Generates a JWT token from Spring Security Authentication object
     * @param authentication - Spring Security Authentication containing user details
     * @return JWT token as String
     */
    public String generateToken(Authentication authentication) {
        // Get username from authentication
        String username = authentication.getName();
        
        // Extract user roles/authorities and convert to list of strings
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        // Get current time for issued at claim
        Date now = new Date();
        
        // Calculate expiration time
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);
        
        // Build and return JWT token
        return Jwts.builder()
                .subject(username)                      // Set username as subject (updated API)
                .issuedAt(now)                         // Set issued time (updated API)
                .expiration(expiryDate)                // Set expiration time (updated API)
                .claim("authorities", authorities)      // Add user roles as custom claim
                .claim("authType", getAuthType(authentication)) // Add authentication type
                .signWith(getSigningKey())             // Sign with secret key (simplified API)
                .compact();                            // Build the token
    }
    
    /**
     * Gets the username from a JWT token
     * 
     * Think of this like opening a sealed envelope and reading who it's addressed to.
     * The JWT token contains the username, and this method extracts it.
     * 
     * @param token - The JWT token (the sealed envelope)
     * @return The username stored inside the token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
    
    /**
     * Gets the user's permissions/roles from the JWT token
     * 
     * This is like checking what access badges someone has. A user might have
     * roles like "ADMIN", "USER", or "MANAGER". This method pulls out all those
     * roles from the token so we know what the user is allowed to do.
     * 
     * @param token - The JWT token containing user information
     * @return A list of roles/permissions (like ["ROLE_USER", "ROLE_ADMIN"])
     */
    @SuppressWarnings("unchecked")
    public List<String> getAuthoritiesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (List<String>) claims.get("authorities");
    }
    
    /**
     * Finds out how the user logged in (LDAP, Database, etc.)
     * 
     * This tells us which system verified the user's password. It's like knowing
     * whether someone entered the building using their employee card (LDAP) or
     * signed in at the front desk (Database).
     * 
     * @param token - The JWT token
     * @return The authentication method used (like "LDAP" or "DATABASE")
     */
    public String getAuthTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return (String) claims.get("authType");
    }
    
    /**
     * Gets expiration date from JWT token
     * @param token - JWT token string
     * @return Date when token expires
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }
    
    /**
     * Validates JWT token
     * @param token - JWT token string
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            // Parse the token - this will throw exception if invalid
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token is invalid - expired, malformed, or signature doesn't match
            System.err.println("JWT validation failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if JWT token is expired
     * @param token - JWT token string
     * @return true if token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (JwtException e) {
            // If we can't parse the token, consider it expired
            return true;
        }
    }
    
    /**
     * Extracts all claims from JWT token
     * This is a private helper method used by other methods
     * @param token - JWT token string
     * @return Claims object containing all token claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()                        // Updated API - parser() instead of parserBuilder()
                .verifyWith(getSigningKey())        // Set the signing key for verification (updated API)
                .build()                            // Build the parser
                .parseSignedClaims(token)          // Parse and verify the token (updated API)
                .getPayload();                      // Get the claims (payload) - updated API
    }
    
    /**
     * Creates the signing key from the secret string
     * @return SecretKey object for signing/verifying tokens (updated for v0.12.x)
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Extracts authentication type from Spring Security Authentication object
     * @param authentication - Spring Security Authentication
     * @return authentication type as String
     */
    private String getAuthType(Authentication authentication) {
        // Check if authentication has details about auth type
        Object details = authentication.getDetails();
        if (details instanceof String) {
            return (String) details;
        }
        
        // Fallback - try to determine from authentication class
        String authClass = authentication.getClass().getSimpleName();
        if (authClass.contains("LDAP")) {
            return "LDAP";
        } else if (authClass.contains("DB") || authClass.contains("Database")) {
            return "DATABASE";
        }
        
        return "UNKNOWN";
    }
}
