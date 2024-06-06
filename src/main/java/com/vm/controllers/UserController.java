package com.vm.controllers;

import com.vm.model.User;
import com.vm.request.UserRequest;
import com.vm.service.UserService;
import com.vm.util.EncryptionUtil;
import com.vm.util.KeyManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<?> updateStatusCourse() throws Exception {
        String message = "Tu van tam ly";
        SecretKey key = KeyManagement.loadKey();
        String encryptedMessage = EncryptionUtil.encrypt(message, key);
        String decryptedMessage = EncryptionUtil.decrypt(encryptedMessage, key);
        return new ResponseEntity<>("Hello !!! " + decryptedMessage, HttpStatus.OK);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        String username = userService.getCurrentUserName();
        return new ResponseEntity<>(userService.getCurrentUser(username), HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<User> update(@RequestBody UserRequest request) throws Exception {
        String username = userService.getCurrentUserName();
        User user = userService.update(request, username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
