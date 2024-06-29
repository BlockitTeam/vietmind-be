package com.vm;

import com.vm.util.AESEncryptionDecryption;
import com.vm.util.EncryptionUtil;
import com.vm.util.KeyManagement;

import javax.crypto.SecretKey;

public class TestEncryptDecrypt {

    public static void main(String[] args) throws Exception {
        String message = "Tu van tam ly";
        SecretKey key = KeyManagement.loadKey();
        String encryptedMessage = EncryptionUtil.encrypt(message, key);
        String decryptedMessage = EncryptionUtil.decrypt(encryptedMessage, key);
        System.out.println("Decrypted Data: " + decryptedMessage);
    }

}
