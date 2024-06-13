package com.vm.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vm.model.Message;
import com.vm.service.MessageService;
import com.vm.service.UserService;
import com.vm.service.impl.MessageServiceImpl;
import com.vm.service.impl.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MessageService messageService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserId(session);
        sessions.put(userId, session);
        logger.info("User {} connected. Session ID: {}", userId, session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = getUserId(session);
        JsonNode jsonMessage = objectMapper.readTree(message.getPayload());
        String targetUserId = jsonMessage.get("targetUserId").asText();
        String msg = jsonMessage.get("message").asText();

        WebSocketSession targetSession = sessions.get(targetUserId);
        if (targetSession != null && targetSession.isOpen()) {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("fromUserId", userId);
            response.put("message", msg);
            targetSession.sendMessage(new TextMessage(response.toString()));
            logger.info("Message from {} to {}: {}", userId, targetUserId, msg);

            //Stored message
            Message request = new Message();
            request.setSenderId(Integer.valueOf(userId));
            request.setReceiverId(Integer.valueOf(targetUserId));
            request.setEncryptedMessage(msg);

            //temp
            request.setConversationId(0);
            request.setIsRead(true);

            messageService.saveMessage(request);
        } else {
            logger.info("Target user {} is not connected.", targetUserId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserId(session);
        sessions.remove(userId);
        logger.info("User {} disconnected. Session ID: {}", userId, session.getId());
    }

    private String getUserId(WebSocketSession session) {
        // Assume userId is passed as a query parameter, e.g., ws://localhost:9001/ws?userId=123
        String userId = session.getUri().getQuery().split("=")[1];
        return userId;
    }
}

