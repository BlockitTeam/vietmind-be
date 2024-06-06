package com.vm.controllers;

import com.vm.dto.BaseResponse;
import com.vm.model.AuthResponse;
import com.vm.model.Response;
import com.vm.request.QuestionObject;
import com.vm.service.QuestionService;
import com.vm.service.ResponseService;
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
    private final ResponseService service;

    @GetMapping("")
    public ResponseEntity<List<Response>> getResponses() {
        List<Response> questions = service.getResponseBySurveyId(1L);
        return ResponseEntity.ok(questions);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<QuestionObject> getTask(@PathVariable Long id) throws Exception {
//        QuestionObject questions = service.getQuestionById(id);
//        return ResponseEntity.ok(questions);
//    }

    @PostMapping("")
    public ResponseEntity<?> saveResponse(@RequestBody List<QuestionObject> request) {
        try {
            service.saveResponse(request);
            return new ResponseEntity<>("Save response successfully", HttpStatus.CREATED);
        }  catch (Exception e) {
            log.error("Failed to save response", e);
            return new ResponseEntity<>("Failed to save response", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("result")
    public ResponseEntity<?> getResult() {
        Map<String, String> result = service.getResult();
        return ResponseEntity.ok(result);
//        return ResponseEntity.ok().body(BaseResponse.success(result, "Get result successfully", HttpStatus.OK.value()));
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteTask() throws Exception {
        try {
            Long surveyId = 1L;
            service.deleteResponses(surveyId);
            return new ResponseEntity<>("Delete successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to delete response", e);
            return new ResponseEntity<>("Failed to save response", HttpStatus.BAD_REQUEST);
        }
    }
//
//    @PutMapping("")
//    public ResponseEntity<Task> updateTask(@RequestBody TaskRequest request) throws Exception {
//        Task Task = taskService.updateTask(request);
//        return new ResponseEntity<>(Task, HttpStatus.OK);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Task> getTask(@PathVariable Long id) throws Exception {
//        Task task =  taskService.findTaskById(id);
//        return new ResponseEntity<>(task, HttpStatus.OK);
//    }
}
