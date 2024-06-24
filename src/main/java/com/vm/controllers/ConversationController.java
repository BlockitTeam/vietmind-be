package com.vm.controllers;

import com.vm.request.PublicKeyRequest;
import com.vm.service.ConversationService;
import com.vm.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/conversation")
@RequiredArgsConstructor
public class ConversationController {
    private final Logger log = LoggerFactory.getLogger(ConversationController.class);
    private final ConversationService conversationService;
    private final MessageService messageService;

    @GetMapping("/{conversation_id}")
    public ResponseEntity<?> getResponses(@PathVariable Integer conversation_id) {
        return ResponseEntity.ok(conversationService.getConversationById(conversation_id));
    }

    @PostMapping("/{conversation_id}/encrypt-key")
    public ResponseEntity<String> encryptConversationKeyForSender(
            @PathVariable Integer conversation_id,
            @RequestBody PublicKeyRequest publicKeyRequest) {
        try {
            String encryptedKey = conversationService.encryptConversationKey(conversation_id, publicKeyRequest.getPublicKey());
            return ResponseEntity.ok(encryptedKey);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{conversation_id}/content")
    public ResponseEntity<?> getContent(@PathVariable Integer conversation_id) {
        return ResponseEntity.ok(messageService.getAllMessByConversationId(conversation_id));
    }
}
