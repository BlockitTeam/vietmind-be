package com.vm.service.impl;

import com.vm.model.Message;
import com.vm.repo.MessageRepository;
import com.vm.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageRepository messageRepo;

    @Override
    public Message saveMessage(Message request) {
        try {
            messageRepo.save(request);
        } catch (Exception ex) {
            System.out.println("Error save message: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Message> getAllMessByConversationId(Integer conversationId) {
        return messageRepo.getAllMessByConversationId(conversationId);
    }
}
