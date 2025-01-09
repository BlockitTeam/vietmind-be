package com.vm.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class FacebookUtils {

    public static String generateAppSecretProof(String accessToken, String appSecret) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(appSecret.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hmac = mac.doFinal(accessToken.getBytes());
            return bytesToHex(hmac);
        } catch (Exception e) {
            throw new RuntimeException("Error generating appsecret_proof", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}