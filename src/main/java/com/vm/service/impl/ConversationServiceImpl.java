package com.vm.service.impl;

import com.vm.model.Conversation;
import com.vm.model.Message;
import com.vm.repo.ConversationRepository;
import com.vm.repo.MessageRepository;
import com.vm.service.ConversationService;
import com.vm.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationServiceImpl implements ConversationService {
    @Autowired
    private ConversationRepository conversationRepo;

    @Override
    public List<Conversation> getConversationByUserId(Long userId) {
        return List.of();
    }

    @Override
    public Conversation saveConversation() {
        return null;
    }

    @Override
    public Conversation getConversationByUserIdAndDoctorId(UUID userId, UUID doctorId) {
        return conversationRepo.getConversationByUserIdAndDoctorId(userId, doctorId);
    }
}
