package com.vm.controllers;

import com.vm.request.QuestionObject;
import com.vm.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/question")
@RequiredArgsConstructor
public class QuestionController {

    private final Logger log = LoggerFactory.getLogger(QuestionController.class);
    private final QuestionService service;

    @GetMapping("")
    public ResponseEntity<List<QuestionObject>> getQuestions() {
        List<QuestionObject> questions = service.getQuestionBySurveyId(1L);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionObject> getQuestionById(@PathVariable Long id) throws Exception {
        QuestionObject questions = service.getQuestionById(id);
        return ResponseEntity.ok(questions);
    }
}
