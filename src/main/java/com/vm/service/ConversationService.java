package com.vm.service;

import com.vm.dto.ConversationWithLastMessageDTO;
import com.vm.model.Conversation;

import java.util.List;
import java.util.Optional;

public interface ConversationService {
	public List<ConversationWithLastMessageDTO> getConversationsWithLastMessageByUserId(String personId);
	public Conversation saveConversation(Conversation conversation) ;
	public Conversation getConversationByUserIdAndDoctorId(String userId, String doctorId);
	public Optional<Conversation> getConversationById(Integer id);
	public String encryptConversationKey(Integer conversationId, String senderPublicKeyString) throws Exception;
	public String getNoteByConversationId(Integer conversationId);
	public void updateNoteByConversationId(Integer conversationId, String note);
}
