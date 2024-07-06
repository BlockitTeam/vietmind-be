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
        try {
            log.info("/conversation by id ---- : ");
            return ResponseEntity.ok(conversationService.getConversationById(conversation_id));
        } catch (Exception e) {
            log.error("/conversation by id error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{conversation_id}/encrypt-key")
    public ResponseEntity<String> encryptConversationKeyForSender(
            @PathVariable Integer conversation_id,
            @RequestBody PublicKeyRequest publicKeyRequest) {
        try {
            log.info("/encrypt-key by conversation_id ---- : ");
            String encryptedKey = conversationService.encryptConversationKey(conversation_id, publicKeyRequest.getPublicKey());
            return ResponseEntity.ok(encryptedKey);
        } catch (Exception e) {
            log.error("/encrypt-key by conversation_id error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{conversation_id}/content")
    public ResponseEntity<?> getContent(@PathVariable Integer conversation_id) {
        try {
            log.info("/content by conversation_id ---- : ");
            return ResponseEntity.ok(messageService.getAllMessByConversationId(conversation_id));
        } catch (Exception e) {
            log.error("/content by conversation_id error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("")
//    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> getAllConversationOfCurrentUser() {
        try {
            log.info("/getAllConversationOfCurrentUser ---- ");
            UUID currentUserId = userService.getCurrentUUID();
            List<ConversationWithLastMessageDTO> conversations = conversationService.getConversationsWithLastMessageByUserId(String.valueOf(currentUserId));
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("/getAllConversationOfCurrentUser error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{conversationId}/note")
    public ResponseEntity<String> getNoteByConversationId(@PathVariable Integer conversationId) {
        try {
            log.info("/note by conversationId ---- : ");
            String note = conversationService.getNoteByConversationId(conversationId);
            return ResponseEntity.ok(note);
        } catch (Exception e) {
            log.error("/note by conversationId error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{conversationId}/note")
    public ResponseEntity<?> updateNoteByConversationId(@PathVariable Integer conversationId, @RequestBody String note) {
        try {
            log.info("/note post data by conversationId ---- : ");
            conversationService.updateNoteByConversationId(conversationId, note);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("/note post data by conversationId error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
