package com.vm.service.impl;

import com.vm.dto.ConversationWithLastMessageDTO;
import com.vm.model.Conversation;
import com.vm.model.Message;
import com.vm.repo.ConversationRepository;
import com.vm.repo.MessageRepository;
import com.vm.service.ConversationService;
import com.vm.util.KeyManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {
    @Autowired
    private ConversationRepository conversationRepo;

    @Autowired
    private MessageRepository messageRepo;

    @Override
    public List<ConversationWithLastMessageDTO> getConversationsWithLastMessageByUserId(String userId, String senderName) {
        List<Object[]> results = conversationRepo.findAllConversationsWithLastMessageByUserId(userId);
        List<ConversationWithLastMessageDTO> conversations = new ArrayList<>();

        for (Object[] result : results) {
            Conversation conversation = (Conversation) result[0];
            Message lastMessage = (Message) result[1];
            String senderFirstName = (String) result[2];
            String senderLastName = (String) result[3];
            String receiverFirstName = (String) result[4];
            String receiverLastName = (String) result[5];
            String senderFullName = senderLastName + " " + senderFirstName;
            String receiverFullName = receiverLastName + " " + receiverFirstName;
            conversations.add(new ConversationWithLastMessageDTO(conversation, lastMessage, senderFullName, receiverFullName, 0));
        }
        // Sort list base on createdAt of Message newest
        conversations.sort((dto1, dto2) -> dto2.getLastMessage().getCreatedAt()
                .compareTo(dto1.getLastMessage().getCreatedAt()));
        
        // Lọc theo senderName nếu được cung cấp
        if (senderName != null && !senderName.trim().isEmpty()) {
            String normalizedSenderName = normalizeString(senderName);
            conversations = conversations.stream()
                    .filter(conversation -> normalizeString(conversation.getSenderFullName())
                            .contains(normalizedSenderName))
                    .collect(Collectors.toList());
        }

        if (conversations.isEmpty())
            return conversations;

        List<Integer> conversationIds= conversations.stream()
                    .map(dto -> dto.getConversation().getConversationId())
                    .collect(Collectors.toList());

        Map<Integer, Long> mapConverWithUnread = getUnreadMessagesCountByConversations(conversationIds, userId);
        conversations.forEach(dto -> {
            Integer conversationId = dto.getConversation().getConversationId();
            // Nếu conversationId tồn tại trong map, gán số lượng unread messages
            dto.setUnreadMessageCount(mapConverWithUnread.getOrDefault(conversationId, 0L).intValue());
        });

        return conversations;
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

    @Override
    public String getNoteByConversationId(Integer conversationId) {
        return conversationRepo.findNoteByConversationId(conversationId);

    }

    @Override
    public void updateNoteByConversationId(Integer conversationId, String note) {
        conversationRepo.updateNoteByConversationId(conversationId, note);
    }

    private String normalizeString(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{M}").matcher(normalized).replaceAll("").toLowerCase();
    }

    public Map<Integer, Long> getUnreadMessagesCountByConversations(List<Integer> conversationIds, String receiverId) {
        List<Map<String, Object>> result = messageRepo.countUnreadMessagesByConversationIdsAndReceiverId(conversationIds, receiverId);

        // Convert List<Map<String, Object>> to Map<Integer, Long>
        return result.stream()
                .collect(Collectors.toMap(
                        entry -> (Integer) entry.get("conversationId"),
                        entry -> (Long) entry.get("unreadCount")
                ));
    }
}