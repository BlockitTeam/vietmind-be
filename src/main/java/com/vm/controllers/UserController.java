package com.vm.controllers;

import com.vm.dto.DoctorDTO;
import com.vm.dto.UserDTO;
import com.vm.model.User;
import com.vm.request.UserRequest;
import com.vm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<?> getCurrentUser() {
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
}
