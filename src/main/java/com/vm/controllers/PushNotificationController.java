package com.vm.controllers;

import com.vm.dto.BaseResponse;
import com.vm.model.User;
import com.vm.repo.UserRepository;
import com.vm.service.PushNotificationService;
import com.vm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/push")
@RequiredArgsConstructor
public class PushNotificationController {
    
    private final PushNotificationService pushNotificationService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(PushNotificationController.class);
    
    /**
     * Send a test push notification to the current user
     */
    @PostMapping("/test")
    public ResponseEntity<BaseResponse<String>> sendTestNotification() {
        try {
            log.info("POST /api/v1/push/test");
            
            String username = userService.getCurrentUserName();
            User user = userService.getCurrentUser(username);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("User not found", HttpStatus.NOT_FOUND.value()));
            }
            
            String deviceToken = user.getDeviceToken();
            if (deviceToken == null || deviceToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error("No device token found for user. Please register a device first.", 
                        HttpStatus.BAD_REQUEST.value()));
            }
            
            // Send test notification
            pushNotificationService.sendSimplePushNotification(
                deviceToken, 
                "Test Notification", 
                "This is a test push notification from VietMind!"
            );
            
            return ResponseEntity.ok(
                BaseResponse.success("Test notification sent successfully", HttpStatus.OK.value())
            );
            
        } catch (Exception e) {
            log.error("Error sending test notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Failed to send test notification: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
    
    /**
     * Send a custom push notification to the current user
     */
    @PostMapping("/send")
    public ResponseEntity<BaseResponse<String>> sendCustomNotification(
            @RequestParam String title,
            @RequestParam String body,
            @RequestParam(required = false) String data) {
        try {
            log.info("POST /api/v1/push/send - Title: {}", title);
            
            String username = userService.getCurrentUserName();
            User user = userService.getCurrentUser(username);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("User not found", HttpStatus.NOT_FOUND.value()));
            }
            
            String deviceToken = user.getDeviceToken();
            if (deviceToken == null || deviceToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error("No device token found for user. Please register a device first.", 
                        HttpStatus.BAD_REQUEST.value()));
            }
            
            // Send custom notification
            if (data != null && !data.isEmpty()) {
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("customData", data);
                pushNotificationService.sendPushNotification(deviceToken, title, body, dataMap);
            } else {
                pushNotificationService.sendSimplePushNotification(deviceToken, title, body);
            }
            
            return ResponseEntity.ok(
                BaseResponse.success("Custom notification sent successfully", HttpStatus.OK.value())
            );
            
        } catch (Exception e) {
            log.error("Error sending custom notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Failed to send custom notification: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
