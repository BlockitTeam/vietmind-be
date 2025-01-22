package com.vm.controllers;

import com.vm.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final MessageService messageService;

    @PostMapping("/markMessageIsRead/{id}")
    public ResponseEntity<?> markMessageIsRead(@PathVariable int id) throws Exception {
        try {
            log.info("/markMessageIsRead by id : {}", id);
            messageService.markMessageIsRead(id);
            return ResponseEntity.ok("Mark message is read successfully");
        }   catch (Exception e) {
            log.error("/markMessageIsRead by id  error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/markMessageIsReadByConverId/{conversation_id}")
    public ResponseEntity<?> markMessageIsReadByConverId(@PathVariable Integer conversation_id) throws Exception {
        try {
            log.info("/markMessageIsReadByConverId: {}", conversation_id);
            messageService.markMessageIsReadByConverId(conversation_id);
            return ResponseEntity.ok("Mark message is read successfully");
        }   catch (Exception e) {
            log.error("/markMessageIsReadByConverId by id  error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
