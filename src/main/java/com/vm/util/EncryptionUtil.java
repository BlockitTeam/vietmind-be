package com.vm.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Arrays;
import java.util.Base64;
import java.security.SecureRandom;

public class EncryptionUtil {

    private static final String AES = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 128; // 128 bits

    public static SecretKey generateKey(int n) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
        keyGenerator.init(n);
        return keyGenerator.generateKey();
    }

    public static String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes());

        byte[] encryptedDataWithIv = new byte[GCM_IV_LENGTH + encryptedData.length];
        System.arraycopy(iv, 0, encryptedDataWithIv, 0, GCM_IV_LENGTH);
        System.arraycopy(encryptedData, 0, encryptedDataWithIv, GCM_IV_LENGTH, encryptedData.length);

        return Base64.getEncoder().encodeToString(encryptedDataWithIv);
    }

    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] iv = Arrays.copyOfRange(decodedData, 0, GCM_IV_LENGTH);
        byte[] encryptedDataWithoutIv = Arrays.copyOfRange(decodedData, GCM_IV_LENGTH, decodedData.length);

        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] decryptedData = cipher.doFinal(encryptedDataWithoutIv);
        return new String(decryptedData);
    }
}
