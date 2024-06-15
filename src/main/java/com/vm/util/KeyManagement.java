package com.vm.util;

import com.vm.config.EncryptionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class KeyManagement {
    private static EncryptionProperties encryptionProperties;

    @Autowired
    public KeyManagement(EncryptionProperties encryptionProperties) {
        this.encryptionProperties = encryptionProperties;
    }

    public void generateAndStoreKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
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
}

