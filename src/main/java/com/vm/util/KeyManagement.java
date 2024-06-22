package com.vm.util;

import com.vm.config.EncryptionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class KeyManagement {
    private static EncryptionProperties encryptionProperties;

    private static final String ALGORITHM = "AES";
    private static final String RSA = "RSA";

    @Autowired
    public KeyManagement(EncryptionProperties encryptionProperties) {
        this.encryptionProperties = encryptionProperties;
    }

    public void generateAndStoreKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        encryptionProperties.setSecretKey(encodedKey);

        // Assuming you will update the properties file manually if needed
    }

    public static SecretKey loadKey() {
        String encodedKey = encryptionProperties.getSecretKey();
        if (encodedKey == null || encodedKey.isEmpty()) {
            throw new IllegalStateException("Secret key is not set in properties");
        }
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256);
        return keyGen.generateKey();
    }

    public static String encryptWithAES(SecretKey aesKey, String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

//    public static String decryptWithAES(SecretKey aesKey, String encryptedData) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.DECRYPT_MODE, aesKey);
//        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
//        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
//        return new String(decryptedBytes);
//    }

    public static SecretKey decryptWithAES(String encryptedKey, SecretKey preInitializedAESKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, preInitializedAESKey);
        byte[] decodedKey = Base64.getDecoder().decode(encryptedKey);
        byte[] decryptedKey = cipher.doFinal(decodedKey);
        return new SecretKeySpec(decryptedKey, 0, decryptedKey.length, ALGORITHM);
    }

    public static PublicKey getPublicKeyFromString(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(spec);
    }

    public static String encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedKey);
    }
}

