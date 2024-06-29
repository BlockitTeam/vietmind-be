package com.vm.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vm.model.Conversation;
import com.vm.model.Message;
import com.vm.service.ConversationService;
import com.vm.service.MessageService;
import com.vm.service.UserService;
import com.vm.service.impl.MyUserDetails;
import com.vm.util.KeyManagement;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String RSA = "RSA";
    private static final String AES = "AES";

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConversationService conversationService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getCurrentUserId(session);
        sessions.put(userId, session);
        logger.info("User {} connected. Session ID: {}", userId, session.getId());

        String doctorId = getTargetUserId(session);
        if (doctorId == null)
            return; //this case user connecting is Doctor

        //Logic new conversation here
        Conversation conversation = conversationService.getConversationByUserIdAndDoctorId(userId, doctorId);
        Integer conversationId;
        if (conversation == null) {
            conversation = new Conversation();
            conversation.setUserId(userId);
            conversation.setDoctorId(doctorId);

            // Generate AES session key and stored
            SecretKey conversationKey = KeyManagement.generateAESKey();
            // Load the pre-initialized AES key from KeyManagement
            SecretKey preInitializedAESKey = KeyManagement.loadKey();

            // Encrypt the conversationKey with the pre-initialized AES key
            String encryptedConversationKey = KeyManagement.encryptWithAES(preInitializedAESKey, Base64.getEncoder().encodeToString(conversationKey.getEncoded()));
            conversation.setEncryptedConversationKey(encryptedConversationKey);
            conversation.setConversationKey(Base64.getEncoder().encodeToString(conversationKey.getEncoded()));

            Conversation newConversation = conversationService.saveConversation(conversation);
            conversationId = newConversation.getConversationId();
        } else {
            conversationId = conversation.getConversationId();
        }
        ObjectNode response = objectMapper.createObjectNode();
        response.put("conversationId", conversationId);
        session.sendMessage(new TextMessage(response.toString()));
    }

    private @Nullable String getCurrentUserId(WebSocketSession session) {
        Object principal = session.getPrincipal();
        String userId = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            userId = ((MyUserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getUserId();
        } else if (principal instanceof OAuth2AuthenticationToken) {
            String username = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttributes().get("email").toString();
            userId = String.valueOf(userService.getUserIdByUserName(username));
        }
        return userId;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = getCurrentUserId(session);
        String targetUserId = getTargetUserId(session);
        JsonNode jsonMessage = objectMapper.readTree(message.getPayload());

        String type = jsonMessage.get("type").asText();
        Integer conversationId = jsonMessage.get("conversationId").asInt();

        if ("message".equals(type)) {
            String msg = jsonMessage.get("message").asText();
            logger.info("Message from {} to {} with conversation id {} : {}", userId, targetUserId, conversationId, msg);

            //Stored message
            Message request = new Message();
            request.setSenderId(userId);
            request.setReceiverId(targetUserId);
            request.setEncryptedMessage(msg);
            request.setConversationId(conversationId);
            request.setIsRead(true);
            messageService.saveMessage(request);
            WebSocketSession targetSession = sessions.get(targetUserId);

            if (targetSession != null && targetSession.isOpen()) {
                //targetUserId resend
                ObjectNode response = objectMapper.createObjectNode();
                response.put("fromUserId", userId);
                response.put("conversationId", conversationId);
                response.put("message", msg);
                response.put("type", type);
                targetSession.sendMessage(new TextMessage(response.toString()));
            } else {
                logger.info("Target user {} is not connected.", targetUserId);
            }
        } else if ("typing".equals(type)) {
            // Handle typing notification
            WebSocketSession targetSession = sessions.get(targetUserId);
            if (targetSession != null) {
                ObjectNode response = objectMapper.createObjectNode();
                response.put("type", "typing");
                response.put("fromUserId", userId);
                response.put("conversationId", conversationId);
                targetSession.sendMessage(new TextMessage(response.toString()));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getCurrentUserId(session);
        sessions.remove(userId);
        logger.info("User {} disconnected. Session ID: {}", userId, session.getId());
    }

    private String getTargetUserId(WebSocketSession session) {
        // Assume userId is passed as a query parameter, e.g., ws://localhost:9001/ws?targetUserId=123
        if (session.getUri().getQuery() == null)
            return null;
        String userId = session.getUri().getQuery().split("=")[1];
        return userId;
    }
}

