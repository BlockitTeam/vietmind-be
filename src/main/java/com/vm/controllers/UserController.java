package com.vm.controllers;

import com.vm.dto.DoctorDTO;
import com.vm.dto.UserDTO;
import com.vm.dto.UserDoctorDTO;
import com.vm.model.User;
import com.vm.request.DoctorUserRequest;
import com.vm.request.PasswordResetRequest;
import com.vm.request.UserRequest;
import com.vm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUserMobile() {
        try {
            log.info("/current-user ---- ");
            String username = userService.getCurrentUserName();
            User user = userService.getCurrentUser(username);
            return new ResponseEntity<>(modelMapper.map(user, UserDTO.class), HttpStatus.OK);
        } catch (Exception e) {
            log.error("/current-user error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("")
    public ResponseEntity<?> update(@RequestBody UserRequest request) throws Exception {
        try {
            log.info("/update user ---- ");
            String username = userService.getCurrentUserName();
            userService.update(request, username);
            return new ResponseEntity<>("Updated successful", HttpStatus.OK);
        } catch (Exception e) {
            log.error("/update user error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/current-user-doctor")
    public ResponseEntity<?> getCurrentUserDoctor() {
        try {
            log.info("/current-user-doctor ---- ");
            String username = userService.getCurrentUserName();
            User user = userService.getCurrentUser(username);
            return new ResponseEntity<>(modelMapper.map(user, UserDoctorDTO.class), HttpStatus.OK);
        } catch (Exception e) {
            log.error("/current-user-doctor error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/doctor")
    public ResponseEntity<?> updateUserDoctor(@RequestBody DoctorUserRequest request) throws Exception {
        try {
            log.info("/doctor/update user ---- ");
            String username = userService.getCurrentUserName();
            userService.updateUserDoctor(request, username);
            return new ResponseEntity<>("Updated successful", HttpStatus.OK);
        } catch (Exception e) {
            log.error("/doctor/update user error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/doctors")
    public ResponseEntity<?> getDoctors() {
        try {
            log.info("/doctors ---- ");
            List<User> doctors = userService.getDoctors();
            return new ResponseEntity<>(doctors.stream()
                    .map(user -> modelMapper.map(user, DoctorDTO.class))
                    .collect(Collectors.toList()), HttpStatus.OK);
        }  catch (Exception e) {
            log.error("/doctors error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/basic-info/{user_id}")
    public ResponseEntity<?> getBasicInfo(@PathVariable String user_id) {
        try {
            log.info("/basic-info ---- ");
            Object result = userService.getBasicInfo(user_id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }  catch (Exception e) {
            log.error("/basic-info error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getDoctorById/{user_id}")
    public ResponseEntity<?> getDoctorById(@PathVariable String user_id) {
        try {
            log.info("/getDoctorById ---- ");
            User user = userService.getDoctorById(user_id).get();
            return new ResponseEntity<>(modelMapper.map(user, DoctorDTO.class), HttpStatus.OK);
        } catch (Exception e) {
            log.error("/getDoctorById error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        try {
            log.info("/reset-password ---- ");
            boolean success = userService.resetPassword(
                    userService.getCurrentUUID(),
                    passwordResetRequest.getCurrentPassword(),
                    passwordResetRequest.getNewPassword()
            );

            if (success) {
                return new ResponseEntity<>("Updated password successful", HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to reset password");
            }
        } catch (Exception e) {
            log.error("/reset-password error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
