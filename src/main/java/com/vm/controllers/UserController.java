package com.vm.controllers;

import com.vm.model.User;
import com.vm.request.UserRequest;
import com.vm.service.impl.CustomOAuth2User;
import com.vm.service.UserService;
import com.vm.util.EncryptionUtil;
import com.vm.util.KeyManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
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
        String username = getUserName();
        return new ResponseEntity<>(userService.getCurrentUser(username), HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<User> updateTask(@RequestBody UserRequest request) throws Exception {
        String username = getUserName();
        User user = userService.update(request, username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else if (principal instanceof CustomOAuth2User) {
                username = ((CustomOAuth2User) principal).getEmail();
            } else if (principal instanceof DefaultOAuth2User) {
                username = ((DefaultOAuth2User) principal).getAttributes().get("email").toString();
            }else {
                username = principal.toString();
            }
        }
        return username;
    }
}
