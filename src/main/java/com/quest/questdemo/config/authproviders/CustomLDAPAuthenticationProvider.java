/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quest.questdemo.config.authproviders;

/**
 *
 * @author MMallick
 */
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.server.UnboundIdContainer;
import org.springframework.stereotype.Component;

@Component
public class CustomLDAPAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private LdapAuthenticationProvider ldapAuthenticationProvider;

    @Autowired
    private CustomDBAuthenticationProvider customDBAuthenticationProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        System.out.println("CUSTOM LDAP AUTHENTICATION : " + username);
        try {
            // Try LDAP authentication first
            return ldapAuthenticationProvider.authenticate(authentication);
        } catch (AuthenticationException ex) {
            // If LDAP authentication fails, fall back to DB authentication
            return customDBAuthenticationProvider.authenticate(authentication);
        }
    }

    @Bean
    public UnboundIdContainer ldapContainer() {
        UnboundIdContainer container = new UnboundIdContainer("dc=springframework,dc=org", "classpath:users.ldif");
        container.setPort(8389); // Ensure this port is not already in use
        return container;
    }

    @Bean
    DefaultSpringSecurityContextSource contextSource(UnboundIdContainer container) {
        return new DefaultSpringSecurityContextSource(
                "ldap://localhost:" + container.getPort() + "/dc=springframework,dc=org");
    }

    @Bean
    BindAuthenticator authenticator(BaseLdapPathContextSource contextSource) {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserDnPatterns(new String[]{"uid={0},ou=people"});
        return authenticator;
    }

    @Bean
    LdapAuthenticationProvider ldapAuthenticationProvider(LdapAuthenticator authenticator) {
        return new LdapAuthenticationProvider(authenticator);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
