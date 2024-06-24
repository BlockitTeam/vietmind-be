package com.vm.service.impl;

import com.vm.model.Conversation;
import com.vm.repo.ConversationRepository;
import com.vm.service.ConversationService;
import com.vm.util.KeyManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationServiceImpl implements ConversationService {
    @Autowired
    private ConversationRepository conversationRepo;

    @Override
    public List<Conversation> getConversationByUserId(Long userId) {
        return List.of();
    }

    @Override
    public Conversation saveConversation(Conversation conversation) {
        return conversationRepo.save(conversation);
    }

    @Override
    public Conversation getConversationByUserIdAndDoctorId(String userId, String doctorId) {
        return conversationRepo.getConversationByUserIdAndDoctorId(userId, doctorId);
    }

    @Override
    public Optional<Conversation> getConversationById(Integer id) {
        return conversationRepo.findById(id);
    }

    @Override
    public String encryptConversationKey(Integer conversationId, String senderPublicKeyString) throws Exception {
        Optional<Conversation> conversationOpt = conversationRepo.findById(conversationId);
        if (!conversationOpt.isPresent()) {
            throw new Exception("Conversation not found");
        }

        Conversation conversation = conversationOpt.get();
        String encryptedConversationKey = conversation.getEncryptedConversationKey();

        // Load pre-initialized AES key
        SecretKey preInitializedAESKey = KeyManagement.loadKey();

        // Decrypt the conversation key
        SecretKey conversationKey = KeyManagement.decryptWithAES(encryptedConversationKey, preInitializedAESKey);

        // Convert senderPublicKeyString to PublicKey
        PublicKey senderPublicKey = KeyManagement.getPublicKeyFromString(senderPublicKeyString);

        // Encrypt the conversation key with sender's public key
        String encryptedConversationKeySender = KeyManagement.encryptAESKeyWithRSA(conversationKey, senderPublicKey);
        return encryptedConversationKeySender;
    }
}