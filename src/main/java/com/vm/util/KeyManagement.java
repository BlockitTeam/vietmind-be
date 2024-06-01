package com.vm.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class KeyManagement {

    private static final String PROPERTIES_FILE = "encryption.properties";
    private static final String SECRET_KEY_PROPERTY = "secret.key";

    public static void generateAndStoreKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        Properties properties = new Properties();
        properties.setProperty(SECRET_KEY_PROPERTY, encodedKey);

        try (FileOutputStream fos = new FileOutputStream(PROPERTIES_FILE)) {
            properties.store(fos, null);
        }
    }

    public static SecretKey loadKey() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(fis);
        }
        String encodedKey = properties.getProperty(SECRET_KEY_PROPERTY);
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

//    public static void main(String[] args) throws Exception {
//        generateAndStoreKey();
//    }
}

