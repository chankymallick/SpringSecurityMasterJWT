package com.quest.questdemo.config;

import com.quest.questdemo.config.authenticationPrePosthandler.CustomOAuth2AuthenticationSuccessHandler;
import com.quest.questdemo.config.authenticationPrePosthandler.CustomOAuth2VerificationFilter;
import com.quest.questdemo.config.authproviders.CustomDBAuthenticationProvider;
import com.quest.questdemo.config.authproviders.CustomLDAPAuthenticationProvider;
import com.quest.questdemo.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.core.annotation.Order;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomDBAuthenticationProvider customDBAuthenticationProvider;

    @Autowired
    private CustomLDAPAuthenticationProvider customLDAPAuthenticationProvider;

    @Autowired
    private CustomOAuth2VerificationFilter customOAuth2VerificationFilter;

    @Autowired
    private CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Original API Security Filter Chain with HTTP Basic Authentication (commented out)
    /*
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // Only apply this filter chain to /api/** paths
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/**").authenticated() // Restricting access to API endpoints
                )
                .csrf().disable()
                .authenticationManager(authenticationManager(http.getSharedObject(AuthenticationManagerBuilder.class)))
                .httpBasic(); // HTTP Basic authentication for API endpoints

        return http.build();
    }
    */

    // New API Security Filter Chain with JWT Authentication
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // Only apply this filter chain to /api/** paths
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/**").authenticated() // Require authentication for API endpoints
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints (stateless)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                ) // Stateless session for JWT
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(customOAuth2VerificationFilter, org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/LoginPage", "/WEB-INF/views/**", "/jwttlogin/**").permitAll() // Removed /api/** - it's handled by API security chain
                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                .loginPage("/LoginPage")
                .successHandler(customOAuth2AuthenticationSuccessHandler)
                .failureUrl("/LoginPage?error=true")
                )
                .authenticationManager(authenticationManager(http.getSharedObject(AuthenticationManagerBuilder.class)))
                .formLogin(formLogin -> formLogin
                .loginPage("/LoginPage")
                .loginProcessingUrl("/login")
                .failureUrl("/LoginPage?error=true")
                .defaultSuccessUrl("/home", true)
                .permitAll()
                )

                .logout(logout -> logout
                .logoutSuccessUrl("/LoginPage")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll())
                .csrf(csrf -> csrf
                .ignoringRequestMatchers("/jwttlogin/**")); // Only disable CSRF for JWT login endpoints


        return http.build();
    }

    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder authenticationManagerBuilder)
            throws Exception {
        authenticationManagerBuilder
                .authenticationProvider(customLDAPAuthenticationProvider) // Add LDAP authentication provider
                .authenticationProvider(customDBAuthenticationProvider); // Add DB authentication provider
        return authenticationManagerBuilder.build();
    }

    @Bean
    public CustomLDAPAuthenticationProvider customLDAPAuthenticationProvider() {
        return new CustomLDAPAuthenticationProvider();
    }

    @Bean
    public CustomDBAuthenticationProvider customAuthenticationProvider() {
        return new CustomDBAuthenticationProvider();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration googleRegistration = ClientRegistration.withRegistrationId("google")
            .clientId("28775352663-051fi3su3i842dh6jn09o8ppr0nircl0.apps.googleusercontent.com")
            .clientSecret("")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("profile", "email") // changed here to include openid and profile
            .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
            .tokenUri("https://oauth2.googleapis.com/token")
            .userInfoUri("https://openidconnect.googleapis.com/v1/userinfo")
            .userNameAttributeName("sub")
            .clientName("Google")
            .build();
        return new InMemoryClientRegistrationRepository(googleRegistration);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository repo) {
        return new InMemoryOAuth2AuthorizedClientService(repo);
    }

}
