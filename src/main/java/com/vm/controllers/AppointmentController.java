package com.vm.controllers;

import com.vm.model.Appointment;
import com.vm.service.AppointmentService;
import com.vm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final Logger log = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<?> createAppointment(@RequestBody Appointment appointment) {
        try {
            log.info("/appointments create ---- ");
            return ResponseEntity.ok(appointmentService.createAppointment(appointment));
        }  catch (Exception e) {
            log.error("/appointments create error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("")
    public ResponseEntity<?> updateAppointment(@RequestBody Appointment appointment) {
        try {
            log.info("/appointments update ---- ");
            return ResponseEntity.ok(appointmentService.createAppointment(appointment));
        }  catch (Exception e) {
            log.error("/appointments update error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/conversation/{conversation_id}")
    public ResponseEntity<?> getAppointmentByConversationId(@PathVariable Integer conversation_id) {
        try {
            log.info("/appointments/conversation by id ---- ");
            return ResponseEntity.ok(appointmentService.getAppointmentByConversationId(conversation_id));
        }  catch (Exception e) {
            log.error("/appointments/conversation by id error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAppointmentByCurrentUser() {
        try {
            log.info("/appointments get appointment of current user ---- ");
            return ResponseEntity.ok(appointmentService.getAppointmentByUserId(userService.getStringCurrentUserId()));
        }  catch (Exception e) {
            log.error("/appointments get appointment of current user error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
