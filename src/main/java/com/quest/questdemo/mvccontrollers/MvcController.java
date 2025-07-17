package com.quest.questdemo.mvccontrollers;


import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class MvcController {

//    @Autowired
//    private UserDetailsService userDetailsService;      

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
            model.addAttribute("profile", oidcUser);
            model.addAttribute("email", oidcUser.getEmail());
            model.addAttribute("name", oidcUser.getFullName() != null ? oidcUser.getFullName() : oidcUser.getGivenName());
            model.addAttribute("picture", oidcUser.getPicture());
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oauth2User) {
            model.addAttribute("profile", oauth2User);
            model.addAttribute("email", oauth2User.getAttribute("email"));
            model.addAttribute("name", oauth2User.getAttribute("name"));
            model.addAttribute("picture", oauth2User.getAttribute("picture"));
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            model.addAttribute("profile", userDetails);
        } else {
            model.addAttribute("profile", principal);
        }
        return "profile";
    }

    @GetMapping("/LoginPage")
    public String login() {        
        return "LoginPage";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("authorities", authentication.getAuthorities());
        // Add OIDC user details if available
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
            model.addAttribute("email", oidcUser.getEmail());
            model.addAttribute("name", oidcUser.getFullName() != null ? oidcUser.getFullName() : oidcUser.getGivenName());
            model.addAttribute("authtype", "Google OIDC");
        } else if (principal instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User oauth2User) {
            model.addAttribute("email", oauth2User.getAttribute("email"));
            model.addAttribute("name", oauth2User.getAttribute("name"));
            model.addAttribute("authtype", "OAuth2");
        } else {
            model.addAttribute("email", "");
            model.addAttribute("name", username);
            model.addAttribute("authtype", authentication.getClass().getSimpleName());
        }
        return "home";
    }
}
