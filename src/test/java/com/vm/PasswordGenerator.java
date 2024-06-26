package com.vm;

import com.vm.model.Conversation;
import com.vm.util.KeyManagement;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import java.util.Base64;

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

}
