package com.vm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FacebookTokenVerifier {

    private static final String FB_TOKEN_VALIDATION_URL = "https://graph.facebook.com/debug_token";
    private static final String FB_USER_INFO_URL = "https://graph.facebook.com/me";
    private static final String APP_ACCESS_TOKEN = "YOUR_APP_ACCESS_TOKEN";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public FacebookTokenVerifier(ObjectMapper objectMapper) {
        this.client = new OkHttpClient();
        this.objectMapper = objectMapper;
    }

    public JsonNode verifyToken(String accessToken, String userId) throws IOException {
        Request request = new Request.Builder()
                .url(FB_TOKEN_VALIDATION_URL + "?input_token=" + accessToken + "&access_token=" + APP_ACCESS_TOKEN)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            JsonNode data = jsonNode.get("data");

            if (data.get("is_valid").asBoolean() && data.get("user_id").asText().equals(userId)) {
                return getUserInfo(accessToken);
            } else {
                throw new SecurityException("Invalid Facebook token");
            }
        }
    }

    private JsonNode getUserInfo(String accessToken) throws IOException {
        Request request = new Request.Builder()
                .url(FB_USER_INFO_URL + "?access_token=" + accessToken + "&fields=id,name,email")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return objectMapper.readTree(response.body().string());
        }
    }
}
