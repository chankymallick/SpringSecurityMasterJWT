# Spring Security LDAP & DB Authentication Providers Demo

## Overview

This project is a Spring MVC (non-Boot) web application demonstrating advanced security with:

- Multiple authentication providers: LDAP and Database (DB)
- Form-based login and OAuth2 login (Google)
- HTTP Basic authentication for REST APIs only
- Custom security configuration using Spring Security 6+
- Example REST API and MVC controllers
- JSP-based views

---

## Features

- **Multiple Authentication Providers:**  
  Users can authenticate using either LDAP or a database. Both providers are registered globally and checked in order.
- **Form Login & OAuth2 Login:**  
  Web users authenticate via a custom login page or Google OAuth2.
- **REST API Security:**  
  All `/api/**` endpoints require HTTP Basic authentication, while web endpoints do not.
- **Custom Security Chains:**  
  Two `SecurityFilterChain` beans are defined to separate API and web security concerns.
- **Configurable OAuth2:**  
  OAuth2 client details can be loaded from `application.properties`.

---

## Project Structure

```
src/
  main/
    java/
      com.quest.questdemo
        config/
          SecurityConfig.java                # Main security configuration
          authenticationPrePosthandler/
            CustomOAuth2VerificationFilter.java
            CustomOAuth2AuthenticationSuccessHandler.java
          authproviders/
            CustomLDAPAuthenticationProvider.java
            CustomDBAuthenticationProvider.java
            DBUserDetailsServices.java
          AppConfig/
            AppInitializer.java
            AppConfig.java
            SecurityWebApplicationInitializer.java
        api/
          RestAPIDemo.java                   # Example REST API controller
        mvccontrollers/
          MvcController.java                 # MVC controller for web pages
    resources/
      application.properties                 # Configuration for DB, LDAP, OAuth2, etc.
      logback.xml
      User&RoleDDL.sql
      users.ldif
    webapp/
      WEB-INF/
        views/
          LoginPage.jsp
          home.jsp
          profile.jsp
        web.xml
      META-INF/
        context.xml
pom.xml                                      # Maven build file
```

---

## Key Files

- **SecurityConfig.java:**  
  Configures two security filter chains:
  - `/api/**`: HTTP Basic only, CSRF disabled.
  - All other endpoints: Form login, OAuth2 login, logout, custom filters.
- **CustomLDAPAuthenticationProvider.java & CustomDBAuthenticationProvider.java:**  
  Custom authentication logic for LDAP and DB.
- **RestAPIDemo.java:**  
  Example REST API controller, returns JSON, protected by HTTP Basic.
- **MvcController.java:**  
  Handles web page requests.
- **application.properties:**  
  Stores configuration for OAuth2, logging, and other properties.

---

## How Authentication Works

- **Web (JSP, MVC):**  
  - Users see a custom login page (`/LoginPage`).
  - Can log in via form (DB or LDAP) or Google OAuth2.
- **REST API (`/api/**`):**  
  - Requires HTTP Basic authentication.
  - Credentials are checked against both LDAP and DB providers.

---

## Test User Credentials

- **Database User:**
  - Username: `mmallick`
  - Password: `520759`
- **LDAP User:**
  - Username: `admin`
  - Password: `password`

---

## How to Run

1. **Configure application.properties:**  
   Set your DB, LDAP, and OAuth2 (Google) credentials.
2. **Build the project:**  
   ```
   mvn clean package
   ```
3. **Deploy the WAR:**  
   Deploy `questdemo-1.0-SNAPSHOT.war` to a servlet container (e.g., Tomcat).
4. **Access the app:**  
   - Web: `http://localhost:8080/questdemo_war_exploded/`
   - API: `http://localhost:8080/questdemo_war_exploded/api/hello` (use HTTP Basic Auth)

---

## Example application.properties

```properties
# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
spring.security.oauth2.client.registration.google.scope=openid,profile,email

# Okta (example)
okta.client-id=...
okta.client-secret=...
okta.authorization-uri=...
okta.token-uri=...
okta.user-info-uri=...

# Logging
logging.level.org.springframework.security=TRACE
logging.level.org.springframework.web=TRACE
```

---

## Customization

- To add more REST APIs, create controllers in `com.quest.questdemo.api`.
- To add more web pages, add JSPs in `src/main/webapp/WEB-INF/views` and map them in `MvcController.java`.
- To change authentication logic, edit the custom provider classes.

---

## License

This project is for educational/demo purposes.
