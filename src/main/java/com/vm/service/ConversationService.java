package com.vm.service;

import com.vm.model.Conversation;

import java.util.List;

public interface ConversationService {
	public List<Conversation> getConversationByUserId(Long userId);
	public Conversation saveConversation() ;
}
