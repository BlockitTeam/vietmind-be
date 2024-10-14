package com.vm.controllers;

import com.vm.request.NewQuestionObject;
import com.vm.request.QuestionObject;
import com.vm.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/question")
@RequiredArgsConstructor
public class QuestionController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final QuestionService service;

    @GetMapping("")
    public ResponseEntity<?> getQuestionsOfGeneralSurvey() {
        try {
            log.info("/question get all ---- : ");
            List<QuestionObject> questions = service.getQuestionBySurveyId(1);
            return ResponseEntity.ok(questions);
        }  catch (Exception e) {
            log.error("/question get all error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) throws Exception {
        try {
            log.info("/question get by id ---- : ");
            QuestionObject questions = service.getQuestionById(id);
            return ResponseEntity.ok(questions);
        }   catch (Exception e) {
            log.error("/question get by id  error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getQuestionsOfStressSurvey")
    public ResponseEntity<?> getQuestionsOfStressSurvey() {
        try {
            log.info("/getQuestionsOfStressSurvey get all ---- : ");
            List<NewQuestionObject> questions = service.getQuestionWithNewFormatBySurveyId(2);
            return ResponseEntity.ok(questions);
        }  catch (Exception e) {
            log.error("/getQuestionsOfStressSurvey get all error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getQuestionsOfUnrestSurvey")
    public ResponseEntity<?> getQuestionsOfUnrestSurvey() {
        try {
            log.info("/getQuestionsOfUnrestSurvey get all ---- : ");
            List<NewQuestionObject> questions = service.getQuestionWithNewFormatBySurveyId(3);
            return ResponseEntity.ok(questions);
        }  catch (Exception e) {
            log.error("/getQuestionsOfUnrestSurvey get all error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getQuestionsOfSleepSurvey")
    public ResponseEntity<?> getQuestionsOfSleepSurvey() {
        try {
            log.info("/getQuestionsOfSleepSurvey get all ---- : ");
            List<NewQuestionObject> questions = service.getQuestionWithNewFormatBySurveyId(4);
            return ResponseEntity.ok(questions);
        }  catch (Exception e) {
            log.error("/getQuestionsOfSleepSurvey get all error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getQuestionsOfDepressionSurvey")
    public ResponseEntity<?> getQuestionsOfDepressionSurvey() {
        try {
            log.info("/getQuestionsOfDepressionSurvey get all ---- : ");
            List<NewQuestionObject> questions = service.getQuestionWithNewFormatBySurveyId(5);
            return ResponseEntity.ok(questions);
        }  catch (Exception e) {
            log.error("/getQuestionsOfDepressionSurvey get all error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getQuestionsOfPTSDSurvey")
    public ResponseEntity<?> getQuestionsOfPTSDSurvey() {
        try {
            log.info("/getQuestionsOfPTSDSurvey get all ---- : ");
            List<NewQuestionObject> questions = service.getQuestionWithNewFormatBySurveyId(6);
            return ResponseEntity.ok(questions);
        }  catch (Exception e) {
            log.error("/getQuestionsOfPTSDSurvey get all error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
