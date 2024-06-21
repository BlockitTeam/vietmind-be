package com.vm.controllers;

import com.vm.model.Response;
import com.vm.request.QuestionObject;
import com.vm.service.ConversationService;
import com.vm.service.ResponseService;
import com.vm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/conversation")
@RequiredArgsConstructor
public class ConversationController {
    private final Logger log = LoggerFactory.getLogger(ConversationController.class);
    private final ConversationService conversationService;

    @GetMapping("/{conversation_id}")
    public ResponseEntity<?> getResponses(@PathVariable Integer conversation_id) {
        return ResponseEntity.ok(conversationService.getConversationById(conversation_id));
    }
}
