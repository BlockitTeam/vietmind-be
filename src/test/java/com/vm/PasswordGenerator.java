package com.vm;

import com.vm.util.AESEncryptionDecryption;

public class PasswordGenerator {

//	public static void main(String[] args) {
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//		String rawPassword = "admin";
//		String encodedPassword = encoder.encode(rawPassword);
//
//		System.out.println(encodedPassword);
//	}

//	public static void main(String[] args) {
//		// Generate AES session key and stored
////		SecretKey conversationKey = KeyManagement.generateAESKey();
//		// Load the pre-initialized AES key from KeyManagement
//		SecretKey preInitializedAESKey = KeyManagement.loadKey();
//
//		// Encrypt the conversationKey with the pre-initialized AES key
//		String encryptedConversationKey = KeyManagement.decryptWithAES(preInitializedAESKey, Base64.getEncoder().encodeToString(conversationKey.getEncoded()));
//		conversation.setEncryptedConversationKey(encryptedConversationKey);
//
//		Conversation newConversation = conversationService.saveConversation(conversation);
//	}

    public static void main(String[] args) throws Exception {
        // Generate AES session key and stored
//		SecretKey conversationKey = KeyManagement.generateAESKey();
//
//        String key = Base64.getEncoder().encodeToString(conversationKey.getEncoded());

        String key = "B3ro445UIwXAAxrcO+V0VnNlQSutJTICFzUxP7cFr2w=";

//        String data = "Hello, World!";
//        System.out.println("Original Data: " + data);

//        String encryptedData = AESEncryptionDecryption.encrypt(data, key);
//        System.out.println("Encrypted Data: " + encryptedData);

        String encryptedData = "b+3CCwd2mDDpV2SlTa72XQ==";

        String decryptedData = AESEncryptionDecryption.decrypt(encryptedData, key);
        System.out.println("Decrypted Data: " + decryptedData);
    }

}
