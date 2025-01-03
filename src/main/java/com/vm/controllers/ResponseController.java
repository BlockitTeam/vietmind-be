package com.vm.controllers;

import com.vm.model.Survey;
import com.vm.request.QuestionObject;
import com.vm.service.AppointmentService;
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
    private final AppointmentService appointmentService;

//    @GetMapping("")
//    public ResponseEntity<?> getResponses() {
//        try {
//            log.info("/response get all ---- : ");
//            List<Response> questions = responseService.getResponseBySurveyId(1L);
//            return ResponseEntity.ok(questions);
//        } catch (Exception e) {
//            log.error("/response get all  error: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }

    @PostMapping("")
    public ResponseEntity<?> saveResponse(@RequestBody List<QuestionObject> request) {
        try {
            log.info("/response save ---- : ");
            responseService.saveResponse(request);
            return new ResponseEntity<>("Save response successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("/response failed to save response", e);
            return new ResponseEntity<>("Failed to save response", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("result")
    public ResponseEntity<?> getResult() {
        try {
            log.info("/result ---- : ");
            Map<String, String> result = responseService.getResult(userService.getStringCurrentUserId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("/result error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("resultDetail")
    public ResponseEntity<?> getResultDetail() {
        try {
            log.info("/resultDetail ---- : ");
            List<QuestionObject> result = responseService.getResultDetail(userService.getStringCurrentUserId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("/resultDetail error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("resultDetailByUserId/{user_id}")
    public ResponseEntity<?> getResultDetailByUserId(@PathVariable("user_id") String userId) {
        try {
            log.info("/resultDetailByUserId ---- : ");
            List<QuestionObject> result = responseService.getResultDetail(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("/resultDetailByUserId error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("getNameOfSurveyDetailByUserId/{user_id}")
    public ResponseEntity<?> getNameOfSurveyDetailByUserId(@PathVariable("user_id") String userId) {
        try {
            log.info("/getNameOfSurveyDetailByUserId ---- : ");
            Map<String, String> result = responseService.getNameOfSurveyDetailByUserId(userId);

            // Kiểm tra giá trị surveyName trong result
            String surveyName = result.get("surveyName");
            if (surveyName == null) {
                log.warn("Survey name not found for userId: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "status", "NOT_FOUND",
                        "message", "Survey not found for the given user.",
                        "userId", userId
                ));
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("/getNameOfSurveyDetailByUserId error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "ERROR",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("result/{user_id}")
    public ResponseEntity<?> getResultByUserId(@PathVariable String user_id) {
        try {
            log.info("/result by user_id ---- : ");
            Map<String, String> result = responseService.getResult(user_id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("/result by user_id error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("")
    public ResponseEntity<?> clearResult() throws Exception {
        try {
            log.info("/result delete---- : ");
            responseService.deleteResponses(userService.getStringCurrentUserId());
            userService.markCompleteGeneralSurvey(false);
            userService.clearInforSurveyDetail();
            appointmentService.deleteAppointmentsByUserId(userService.getStringCurrentUserId());
            return new ResponseEntity<>("Delete successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("/result failed to delete response", e);
            return new ResponseEntity<>("Failed to delete response", HttpStatus.BAD_REQUEST);
        }
    }
}
