package com.vm.controllers;

import com.vm.model.Question;
import com.vm.request.QuestionObject;
import com.vm.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

//    @PostMapping("")
//    public ResponseEntity<Task> createTask(@RequestBody TaskRequest request) {
//        Task task = taskService.addTask(request);
//        return new ResponseEntity<>(task, HttpStatus.CREATED);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteTask(@PathVariable Long id) throws Exception {
//        taskService.deleteTask(id);
//        return ResponseEntity.ok().body(BaseResponse.success(null, "Delete successfully", HttpStatus.OK.value()));
//    }
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
