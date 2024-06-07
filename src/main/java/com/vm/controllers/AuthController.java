package com.vm.controllers;

import com.vm.model.AuthResponse;
import com.vm.service.FacebookAuthService;
import com.vm.service.GoogleTokenVerifier;
import com.vm.service.UserService;
import com.vm.util.TokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    @Autowired
    private FacebookAuthService facebookAuthService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenStore tokenStore;

    @PostMapping("")
    public ResponseEntity<?> authenticate(@RequestBody TokenRequest tokenRequest) {
        try {
            String token = tokenRequest.getToken();
            // Check if token has already been used
            if (tokenStore.isTokenUsed(token)) {
                return new ResponseEntity<>("Token has already been used", HttpStatus.UNAUTHORIZED);
            }
            String provider = tokenRequest.getProvider();
            switch (provider.toLowerCase()) {
                case "google":
                    return ResponseEntity.ok(googleTokenVerifier.authenticate(token, tokenStore));
                case "facebook":
                    return ResponseEntity.ok(facebookAuthService.authenticate(token, tokenStore));
                default:
                    return ResponseEntity.badRequest().body("Unsupported provider");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Invalid ID token"));
        }
    }

    public static class TokenRequest {
        private String token;
        private String provider;

        public String getToken() {
            return token;
        }

        public String getProvider() {
            return provider;
        }
    }

    @GetMapping("/session-timeout")
    public String getSessionTimeout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        int timeout = session.getMaxInactiveInterval();
        return "Session timeout is " + timeout + " seconds";
    }
}
