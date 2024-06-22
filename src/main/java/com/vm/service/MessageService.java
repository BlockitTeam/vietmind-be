package com.vm.service;

import com.vm.model.Message;

import java.util.List;

public interface MessageService {
	public Message saveMessage(Message request) ;
	public List<Message> getAllMessByConversationId(Integer conversationId);
}
