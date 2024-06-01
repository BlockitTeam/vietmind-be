package com.vm.controllers;

import com.vm.model.AuthResponse;
import com.vm.service.FacebookAuthService;
import com.vm.service.GoogleTokenVerifier;
import com.vm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("")
    public ResponseEntity<?> authenticate(@RequestBody TokenRequest tokenRequest) {
        try {
            String token = tokenRequest.getToken();
            String provider = tokenRequest.getProvider();
            switch (provider.toLowerCase()) {
                case "google":
                    return ResponseEntity.ok(googleTokenVerifier.authenticate(token));
                case "facebook":
                    return ResponseEntity.ok(facebookAuthService.authenticate(token));
                default:
                    return ResponseEntity.badRequest().body("Unsupported provider");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Invalid ID token"));
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
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
}
