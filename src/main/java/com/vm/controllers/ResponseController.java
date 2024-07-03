package com.vm.controllers;

import com.vm.model.Response;
import com.vm.request.QuestionObject;
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
@RequestMapping("/api/v1/response")
@RequiredArgsConstructor
public class ResponseController {
    private final Logger log = LoggerFactory.getLogger(ResponseController.class);
    private final ResponseService responseService;
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<Response>> getResponses() {
        List<Response> questions = responseService.getResponseBySurveyId(1L);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("")
    public ResponseEntity<?> saveResponseForGeneralSurvey(@RequestBody List<QuestionObject> request) {
        try {
            responseService.saveResponse(request);
            userService.markCompleteGeneralSurvey(true);
            return new ResponseEntity<>("Save response successfully", HttpStatus.CREATED);
        }  catch (Exception e) {
            log.error("Failed to save response", e);
            return new ResponseEntity<>("Failed to save response", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("result")
    public ResponseEntity<?> getResult() {
        Map<String, String> result = responseService.getResult();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("")
    public ResponseEntity<?> clearResult() throws Exception {
        try {
            Long surveyId = 1L;
            responseService.deleteResponses(surveyId);
            userService.markCompleteGeneralSurvey(false);
            return new ResponseEntity<>("Delete successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to delete response", e);
            return new ResponseEntity<>("Failed to delete response", HttpStatus.BAD_REQUEST);
        }
    }
}
