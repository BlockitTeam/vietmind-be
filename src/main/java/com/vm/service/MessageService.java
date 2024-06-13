package com.vm.service;

import com.vm.model.Conversation;
import com.vm.model.Message;

import java.util.List;

public interface MessageService {
//	public List<Conversation> getConversationByUserId(Long userId);
	public Message saveMessage(Message request) ;
}
