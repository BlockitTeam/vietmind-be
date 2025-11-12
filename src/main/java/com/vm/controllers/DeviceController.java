package com.vm.controllers;

import com.vm.dto.BaseResponse;
import com.vm.model.User;
import com.vm.repo.UserRepository;
import com.vm.request.DeviceRequest;
import com.vm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/device")
@RequiredArgsConstructor
public class DeviceController {
    
    private final UserService userService;
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(DeviceController.class);
    
    /**
     * Register or update device token for Firebase push notifications
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<String>> registerDeviceToken(@RequestBody DeviceRequest request) {
        try {
            log.info("POST /api/v1/device/register - Device Token: {}", request.getDeviceToken());
            
            // Validate device token
            if (request.getDeviceToken() == null || request.getDeviceToken().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error("Device token is required", HttpStatus.BAD_REQUEST.value()));
            }
            
            String username = userService.getCurrentUserName();
            User user = userService.getCurrentUser(username);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("User not found", HttpStatus.NOT_FOUND.value()));
            }
            
            // Update the device token for the current user
            user.setDeviceToken(request.getDeviceToken());
            userRepository.save(user);
            
            log.info("Device token updated successfully for user: {}", username);
            
            return ResponseEntity.ok(
                BaseResponse.success("Device token registered successfully", HttpStatus.OK.value())
            );
            
        } catch (Exception e) {
            log.error("Error registering device token: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Failed to register device token: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
    
    /**
     * Get current user's device token
     */
    @GetMapping("/token")
    public ResponseEntity<BaseResponse<String>> getDeviceToken() {
        try {
            log.info("GET /api/v1/device/token");
            
            String username = userService.getCurrentUserName();
            User user = userService.getCurrentUser(username);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("User not found", HttpStatus.NOT_FOUND.value()));
            }
            
            String deviceToken = user.getDeviceToken();
            
            return ResponseEntity.ok(
                BaseResponse.success(deviceToken, "Device token retrieved successfully", HttpStatus.OK.value())
            );
            
        } catch (Exception e) {
            log.error("Error getting device token: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Failed to get device token: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
    
    /**
     * Remove device token (logout from device)
     */
    @DeleteMapping("/token")
    public ResponseEntity<BaseResponse<String>> removeDeviceToken() {
        try {
            log.info("DELETE /api/v1/device/token");
            
            String username = userService.getCurrentUserName();
            User user = userService.getCurrentUser(username);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error("User not found", HttpStatus.NOT_FOUND.value()));
            }
            
            // Clear the device token
            user.setDeviceToken(null);
            userRepository.save(user);
            
            log.info("Device token removed successfully for user: {}", username);
            
            return ResponseEntity.ok(
                BaseResponse.success("Device token removed successfully", HttpStatus.OK.value())
            );
            
        } catch (Exception e) {
            log.error("Error removing device token: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Failed to remove device token: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
