package com.vm.controllers;

import com.vm.dto.AppointmentEventDTO;
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

import java.util.List;
import java.util.Optional;

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
            String userId = userService.getStringCurrentUserId();
            Optional<Appointment> appointment = appointmentService.getAppointmentByUserId(userId);
            if (appointment.isPresent()) {
                return ResponseEntity.ok(appointment.get());
            } else {
                // Trả về phản hồi trống với HTTP 200 và thông báo
                return ResponseEntity.ok("No appointments found for the current user.");
            }
        }  catch (Exception e) {
            log.error("/appointments get appointment of current user error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/doctor")
    public ResponseEntity<?> getAppointmentsByCurrentDoctor() {
        try {
            log.info("/appointments/doctor get appointment of current doctor ---- ");
            List<AppointmentEventDTO> events = appointmentService.getAppointmentsByDoctorId(userService.getStringCurrentUserId());
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch appointments for doctorId: " + userService.getStringCurrentUserId());
        }
    }

    @GetMapping("/finished")
    public ResponseEntity<?> getFinishedAppointmentsByCurrentUser() {
        try {
            log.info("/appointments/finished ---- Get finished appointments for current user");
            String userId = userService.getStringCurrentUserId();
            List<Appointment> finishedAppointments = appointmentService.getFinishedAppointmentsByUserId(userId);

            if (finishedAppointments.isEmpty()) {
                return ResponseEntity.ok("No finished appointments found for the current user.");
            }
            return ResponseEntity.ok(finishedAppointments);
        } catch (Exception e) {
            log.error("/appointments/finished error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/doctor/currentAppointment/{userId}")
    public ResponseEntity<?> forDoctorGetCurrentAppointmentByUserId(@PathVariable String userId) {
        try {
            log.info("/appointments/doctor/currentAppointment/{} ---- get current appointment of user", userId);
            // Lấy cuộc hẹn mới nhất
            Optional<Appointment> currentAppointment = appointmentService.getCurrentAppointmentByUserId(userId);

            if (currentAppointment.isPresent()) {
                return ResponseEntity.ok(currentAppointment.get());
            }
            return ResponseEntity.ok("No appointments found for the userId.");
        } catch (Exception e) {
            log.error("/appointments/doctor/past/{} error: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/doctor/futureAppointment/{userId}")
    public ResponseEntity<?> forDoctorGetFutureAppointmentByUserId(@PathVariable String userId) {
        try {
            log.info("/appointments/doctor/futureAppointment/{} ---- get future appointment of user", userId);
            Optional<Appointment> futureAppointment = appointmentService.getFutureAppointmentByUserId(userId);
            if (futureAppointment.isPresent()) {
                return ResponseEntity.ok(futureAppointment.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No future appointments found.");
            }
        } catch (Exception e) {
            log.error("/appointments/doctor/futureAppointment error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
