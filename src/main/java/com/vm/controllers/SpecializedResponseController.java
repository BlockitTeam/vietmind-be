package com.vm.controllers;

import com.vm.model.SpecializedResponse;
import com.vm.request.QuestionObject;
import com.vm.service.UserService;
import com.vm.service.impl.SpecializedResponseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specialized-responses")
public class SpecializedResponseController {
    private final Logger log = LoggerFactory.getLogger(ResponseController.class);

    @Autowired
    private SpecializedResponseService specializedResponseService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<SpecializedResponse> createSpecializedResponse(@RequestParam String userId,
                                                                         @RequestParam int surveyId,
                                                                         @RequestParam Long optionId) {
        SpecializedResponse response = specializedResponseService.saveSpecializedResponse(userId, surveyId, optionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latestResultDetail")
    public ResponseEntity<?> getLatestSpecializedResponse(@RequestParam String userId) {
        try {
            log.info("/api/v1/specialized-responses/latestResultDetail ---- : ");
            List<QuestionObject> responses = specializedResponseService.getLatestSpecializedResponse(userId);
            if (responses.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("/api/v1/specialized-responses/latestResultDetail error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @PostMapping("save")
    public ResponseEntity<?> saveSpecializedResponse(@RequestBody List<QuestionObject> request) {
        try {
            log.info("/api/v1/specialized-responses/save ---- : ");
            specializedResponseService.saveResponse(request);
            return new ResponseEntity<>("Save specialized response successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("/api/v1/specialized-responses/save failed to save specialized response", e);
            return new ResponseEntity<>("Failed to save specialized response", HttpStatus.BAD_REQUEST);
        }
    }
}

