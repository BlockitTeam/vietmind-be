package com.vm.controllers;

import com.vm.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/survey")
@RequiredArgsConstructor
public class SurveyController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SurveyService surveyService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getSurveyDetailById(@PathVariable Integer id) throws Exception {
        try {
            log.info("/survey get by id ---- : ");
            return surveyService.findSurveyById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }   catch (Exception e) {
            log.error("/survey get by id  error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
