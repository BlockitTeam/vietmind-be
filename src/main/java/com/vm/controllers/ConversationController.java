package com.vm.controllers;

import com.vm.dto.ConversationWithLastMessageDTO;
import com.vm.request.PublicKeyRequest;
import com.vm.service.ConversationService;
import com.vm.service.MessageService;
import com.vm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conversation")
@RequiredArgsConstructor
public class ConversationController {
    private final Logger log = LoggerFactory.getLogger(ConversationController.class);
    private final ConversationService conversationService;
    private final MessageService messageService;
    private final UserService userService;

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

    @GetMapping("")
//    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> getAllConversationOfCurrentUser() {
        try {
            UUID currentUserId = userService.getCurrentUserId();
            List<ConversationWithLastMessageDTO> conversations = conversationService.getConversationsWithLastMessageByUserId(String.valueOf(currentUserId));
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
