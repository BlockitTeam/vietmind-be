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
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service
public class FacebookAuthService {

    private static final String CLIENT_ID = "1547044715861002";
    private static final String CLIENT_SECRET = "1068fc4d217f914d44f50a4e7be9c79e";
    private static final String USER_INFO_URL = "https://graph.facebook.com/me?fields=id,name,email&access_token=";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    public String getAppAccessToken() throws IOException, InterruptedException {
        String url = "https://graph.facebook.com/oauth/access_token?" +
                "client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, StandardCharsets.UTF_8) +
                "&grant_type=client_credentials";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            return jsonResponse.getString("access_token");
        } else {
            throw new IOException("Failed to fetch access token, response code: " + response.statusCode());
        }
    }

    public JSONObject verifyToken(String userToken) throws IOException, InterruptedException {
        String appAccessToken = getAppAccessToken();
        String verifyUrl = USER_INFO_URL + URLEncoder.encode(userToken, StandardCharsets.UTF_8);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(verifyUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JSONObject userJson = new JSONObject(response.body());
            String userId = userJson.getString("id");
            String userName = userJson.getString("name");
            String userEmail = userJson.optString("email", userId);

            JSONObject attributes = new JSONObject();
            attributes.put("id", userId);
            attributes.put("name", userName);
            attributes.put("email", userEmail);

            return attributes;
        } else if (response.statusCode() == 400) {
            //Try another way due to token from IOS
            DecodedJWT decodedJWT = JWT.decode(userToken);
            String userId = decodedJWT.getClaim("sub").asString();
            String userName = decodedJWT.getClaim("name").asString();
            String userEmail = decodedJWT.getClaim("email").asString();

            JSONObject attributes = new JSONObject();
            attributes.put("id", userId);
            attributes.put("name", userName);
            attributes.put("email", userEmail);
            return attributes;
        } else {
                throw new IOException("Failed to verify user token, response code: " + response.statusCode());
        }
    }

    public AuthResponse authenticate(String userToken, TokenStore tokenStore) throws Exception {
        try {
            JSONObject objectResult = verifyToken(userToken);
            String email = objectResult.getString("email");
            // Check if user exists in your database
            userService.processOAuthPostLogin(email, Provider.FACEBOOK);

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
