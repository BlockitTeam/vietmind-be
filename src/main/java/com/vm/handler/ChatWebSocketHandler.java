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
import com.vm.util.EncryptionUtil;
import com.vm.util.KeyManagement;
import com.vm.util.KeyUtils;
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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.PublicKey;
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

        //Logic save conversation here
        String doctorId = getTargetUserId(session);
        UUID userUUID = UUID.fromString(userId);
        UUID doctorUUID = UUID.fromString(doctorId);

        Conversation conversation = conversationService.getConversationByUserIdAndDoctorId(userUUID, doctorUUID);
        Integer conversationId;
        if (conversation == null) {
            conversation = new Conversation();
            conversation.setUserId(userUUID);
            conversation.setDoctorId(doctorUUID);

            // Generate AES session key and stored
            SecretKey conversationKey = KeyManagement.generateAESKey();
            // Load the pre-initialized AES key from KeyManagement
            SecretKey preInitializedAESKey = KeyManagement.loadKey();

            // Encrypt the conversationKey with the pre-initialized AES key
            String encryptedConversationKey = KeyManagement.encryptWithAES(preInitializedAESKey, Base64.getEncoder().encodeToString(conversationKey.getEncoded()));
            conversation.setEncryptedConversationKey(encryptedConversationKey);

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

        // Parse the payload (you can use a library like Jackson to parse JSON)
        // For simplicity, let's assume the payload is a JSON string like:
        // {"type":"message", "targetUserId":"user2", "message":"Hello"}
        // or
        // {"type":"typing", "targetUserId":"user2"}

        String type = jsonMessage.get("type").asText();
        if ("message".equals(type)) {
            String msg = jsonMessage.get("message").asText();
            WebSocketSession targetSession = sessions.get(targetUserId);
            if (targetSession != null && targetSession.isOpen()) {
                logger.info("Message from {} to {}: {}", userId, targetUserId, msg);

                //Stored message
                Message request = new Message();
                request.setSenderId(Integer.valueOf(userId));
                request.setReceiverId(Integer.valueOf(targetUserId));

                //Encrypt message
                SecretKey key = KeyManagement.loadKey();
                String encryptedMessage = EncryptionUtil.encrypt(msg, key);
                request.setEncryptedMessage(encryptedMessage);
                //temp
                request.setConversationId(0);
                request.setIsRead(true);

                messageService.saveMessage(request);

                //targetUserId resend
                String decryptedMessage = EncryptionUtil.decrypt(encryptedMessage, key);
                ObjectNode response = objectMapper.createObjectNode();
                response.put("fromUserId", userId);
                response.put("message", decryptedMessage);
                response.put("type", type);

                targetSession.sendMessage(new TextMessage(response.toString()));
            } else {
                logger.info("Target user {} is not connected.", targetUserId);
            }
        } else if ("typing".equals(type)) {
            // Handle typing notification
            WebSocketSession targetSession = sessions.get(targetUserId);
            if (targetSession != null) {
                targetSession.sendMessage(new TextMessage("{\"type\":\"typing\", \"fromUserId\":\"" + userId + "\"}"));
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
        String userId = session.getUri().getQuery().split("=")[1];
        return userId;
    }
}

