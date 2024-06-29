package com.vm.controllers;

import com.vm.dto.UserDTO;
import com.vm.model.User;
import com.vm.request.UserRequest;
import com.vm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        String username = userService.getCurrentUserName();
        return new ResponseEntity<>(userService.getCurrentUser(username), HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<?> update(@RequestBody UserRequest request) throws Exception {
        String username = userService.getCurrentUserName();
        userService.update(request, username);
        return new ResponseEntity<>("Updated successful", HttpStatus.OK);
    }
    @GetMapping("/doctors")
    public ResponseEntity<?> getDoctors() {
        List<User> doctors = userService.getDoctors();
        ModelMapper modelMapper = new ModelMapper();
        return new ResponseEntity<>(doctors.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList()), HttpStatus.OK);
    }
}
