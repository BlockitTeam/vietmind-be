package com.vm.service;

import com.vm.model.Conversation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationService {
	public List<Conversation> getConversationByUserId(Long userId);
	public Conversation saveConversation(Conversation conversation) ;
	public Conversation getConversationByUserIdAndDoctorId(UUID userId, UUID doctorId);
	public Optional<Conversation> getConversationById(Integer id);
	public String encryptConversationKey(Integer conversationId, String senderPublicKeyString) throws Exception;
}
