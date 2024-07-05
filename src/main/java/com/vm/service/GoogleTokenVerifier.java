package com.vm.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager.Builder;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.vm.constant.Provider;
import com.vm.model.AuthResponse;
import com.vm.util.TokenStore;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

@Service
public class GoogleTokenVerifier {
    @Autowired
    private UserService userService;
    private final GoogleIdTokenVerifier verifier;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public GoogleTokenVerifier(@Value("${custom.client-id-google-ios}") String CLIENT_ID_IOS,
                               @Value("${custom.client-id-google-android}") String CLIENT_ID_ANDROID,
                               @Value("${custom.client-id-google-web}") String CLIENT_ID_WEB) throws Exception {
        NetHttpTransport transport = new NetHttpTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GooglePublicKeysManager publicKeysManager = new Builder(transport, jsonFactory)
                .setPublicCertsEncodedUrl("https://www.googleapis.com/oauth2/v1/certs")
                .build();

        try {
            publicKeysManager.refresh();
        } catch (GeneralSecurityException | IOException e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }

        this.verifier = new GoogleIdTokenVerifier.Builder(publicKeysManager)
                .setAudience(Arrays.asList(CLIENT_ID_IOS, CLIENT_ID_ANDROID, CLIENT_ID_WEB))
                .build();
    }

    public GoogleIdToken.Payload verify(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload();
        } else {
            throw new SecurityException("Invalid ID token.");
        }
    }

    public AuthResponse authenticate(String userToken, TokenStore tokenStore) throws Exception {
        try {
            GoogleIdToken.Payload payload = verify(userToken);

            String userId = payload.getSubject();
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            // Check if user exists in your database
            userService.processOAuthPostLogin(email, Provider.GOOGLE);

            // Authenticate the user
            OAuth2User oAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), payload, "sub");

            // Create an OAuth2AuthenticationToken
            OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                    oAuth2User,
                    oAuth2User.getAuthorities(),
                    "google"
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("/login successfully ---- : {}", email);
            tokenStore.markTokenAsUsed(userToken);
            return new AuthResponse("Authentication successful");
        } catch (Exception exception) {
            log.error("Failed to authenticate : {}", exception.getMessage());
            throw exception;
        }
    }
}
