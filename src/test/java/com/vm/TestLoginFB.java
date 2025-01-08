package com.vm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vm.util.EncryptionUtil;
import com.vm.util.KeyManagement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.crypto.SecretKey;

public class TestLoginFB {

    public static void main(String[] args) throws Exception {
        System.out.println("Start testing: ");
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImFlYzM5NjU4ZTU0NDIzNzY2MTFmMDY5OGE4ODZkZjk2MDZjMDNhN2MifQ.eyJpc3MiOiJodHRwczpcL1wvd3d3LmZhY2Vib29rLmNvbSIsImF1ZCI6IjE2Nzc2NTE4MDk0MzYyNDAiLCJzdWIiOiIyNDkwNDEzNzQ0NjgxMTQ5IiwiaWF0IjoxNzM2MzI0NDgyLCJleHAiOjE3MzYzMjgwODIsImp0aSI6IkF3eTEuODRhYmIwMjJmODNkMzgzMzRlMGUzNGU1NWZlZjkwZTZiN2I3ZWJhM2YzNjZlM2E2NGQwYzgyMjdlNGRmZWQzMyIsIm5vbmNlIjoibXlfbm9uY2UiLCJlbWFpbCI6InJpbm5ndXllbjk4LnB5XHUwMDQwZ21haWwuY29tIiwiZ2l2ZW5fbmFtZSI6IlJpbiIsImZhbWlseV9uYW1lIjoiTmd1eVx1MWVjNW4iLCJuYW1lIjoiTmd1eVx1MWVjNW4gUmluIiwicGljdHVyZSI6Imh0dHBzOlwvXC9wbGF0Zm9ybS1sb29rYXNpZGUuZmJzYnguY29tXC9wbGF0Zm9ybVwvcHJvZmlsZXBpY1wvP2FzaWQ9MjQ5MDQxMzc0NDY4MTE0OSZoZWlnaHQ9MTAwJndpZHRoPTEwMCZleHQ9MTczODkxNjQ4MyZoYXNoPUFiYTRndEVOTzlZcHZGaXNTRkdBUmlmcSJ9.nkNPkuuoOUrzYwi20Em4lVZyMey91GSosp_STOxcNdKIJEdm_-TkPPgKDdkF4P1w9_sRmVBUs8cWW2ZgqDTOC_xPcALHBJG5lDOgDHscUC5Lov4Fww2mD-JK8safTwwhh8givpQkB6p7b0AHNRwdem3MhuEEEIoISqoqlDzqO9KNheTWka1-mcBW8bO0aPiq3MGsXTLRJbJOC200wJ0ZY7Gj0Qhw6XLoVDwt8lfu8Gu9zU1yUAm4d6P6VWa_QK5F-oN245UI2b8Gk0nRedLMHd6oVwRhj1xpFguLVJyBnSiF4w4RQsKXomRwE3m4AG-t_Spj-vQGm5Ka0Rl8Q7jdvQ";


        DecodedJWT decodedJWT = JWT.decode(token);

        // In thông tin payload
        System.out.println("Token Payload: " + decodedJWT.getPayload());
        System.out.println("User ID: " + decodedJWT.getClaim("sub").asString());
        System.out.println("Email: " + decodedJWT.getClaim("email").asString());


        boolean isValid = validateFacebookToken(token);
    }

    public static boolean validateFacebookToken(String authToken) {
        try {
            String appId = "1677651809436240";
            String appSecret = "4d15eb4e74e54bfe75bfdfb146e7fe8f";

            String url = "https://graph.facebook.com/debug_token?input_token=" + authToken
                    + "&access_token=" + appId + "|" + appSecret;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode data = root.path("data");

            return data.path("is_valid").asBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
