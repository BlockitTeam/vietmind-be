package com.vm.service.impl;

import com.vm.model.Message;
import com.vm.repo.MessageRepository;
import com.vm.service.MessageService;
import com.vm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private UserService userService;

    @Override
    public Message saveMessage(Message request) {
        try {
            return messageRepo.save(request);
        } catch (Exception ex) {
            System.out.println("Error save message: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Message> getAllMessByConversationId(Integer conversationId) {
        return messageRepo.getAllMessByConversationId(conversationId);
    }

    @Override
    public void markMessageIsRead(int messageId) {
        String receiverId = userService.getStringCurrentUserId();
        messageRepo.markMessagesAsRead(messageId, receiverId);
    }
}
