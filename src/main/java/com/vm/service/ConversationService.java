package com.vm.service;

import com.vm.model.Conversation;

import java.util.List;
import java.util.UUID;

public interface ConversationService {
	public List<Conversation> getConversationByUserId(Long userId);
	public Conversation saveConversation() ;
	public Conversation getConversationByUserIdAndDoctorId(UUID userId, UUID doctorId);
}
