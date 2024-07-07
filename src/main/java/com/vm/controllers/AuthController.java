package com.vm.controllers;

import com.vm.model.AuthResponse;
import com.vm.request.LoginRequest;
import com.vm.request.TokenRequest;
import com.vm.service.FacebookAuthService;
import com.vm.service.GoogleTokenVerifier;
import com.vm.service.UserService;
import com.vm.util.TokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final Logger log = LoggerFactory.getLogger(AuthController.class);

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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @PostMapping("")
    public ResponseEntity<?> authenticate(@RequestBody TokenRequest tokenRequest) {
        try {
            log.info("/auth ---- ");
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
            log.error("/auth error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Invalid ID token"));
        }
    }

    @GetMapping("/session-timeout")
    public String getSessionTimeout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        int timeout = session.getMaxInactiveInterval();
        return "Session timeout is " + timeout + " seconds";
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            authenticationSuccessHandler.onAuthenticationSuccess(null, response, authentication);
            log.info("/login successfully ---- : " + loginRequest.getUsername());
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            log.error("/login error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (Exception e) {
            log.error("/login error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("/logout ---- ");
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                new SecurityContextLogoutHandler().logout(request, response, authentication);
            }
            // Clear the JSESSIONID cookie
            ResponseCookie cookie = ResponseCookie.from("JSESSIONID", null)
                    .path("/")
                    .httpOnly(true)
                    .maxAge(0)  // Set the max age to 0 to delete the cookie
//                    .secure(true)  // Set this according to your needs, usually true for HTTPS
//                    .sameSite("Lax")  // Set the same site policy as required
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return ResponseEntity.ok("{\"message\": \"Logout successful\"}");
        } catch (Exception e) {
            log.error("/logout error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
