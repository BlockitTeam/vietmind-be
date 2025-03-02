package com.vm.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vm.constant.Provider;
import com.vm.model.AuthResponse;
import com.vm.util.TokenStore;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Service
public class AppleAuthService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    public JSONObject verifyToken(String userToken) throws IOException, InterruptedException {
        try {
            //Try another way due to token from IOS
            DecodedJWT jwt = JWT.decode(userToken);
            String userId = jwt.getSubject(); // ID của user (Apple ID)
            String userName = jwt.getClaim("email").asString(); // Email của user
            String userEmail = jwt.getSubject(); // ID của user (Apple ID)

            JSONObject attributes = new JSONObject();
            attributes.put("id", userId);
            attributes.put("name", userName);
            attributes.put("email", userEmail);
            return attributes;
        } catch (Exception e) {
            throw new IOException("Failed to verify user token from apple: ");
        }
    }

    public AuthResponse authenticate(String userToken, TokenStore tokenStore) throws Exception {
        try {
            JSONObject objectResult = verifyToken(userToken);
            String email = objectResult.getString("email");
            // Check if user exists in your database
            userService.processOAuthPostLogin(email, Provider.APPLE);

            OAuth2User oAuth2User = new DefaultOAuth2User(
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                    objectResult.toMap(),
                    "name"
            );

            Authentication authentication = new OAuth2AuthenticationToken(oAuth2User, oAuth2User.getAuthorities(), "facebook");
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
