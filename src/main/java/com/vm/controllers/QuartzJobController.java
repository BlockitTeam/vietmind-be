package com.vm.controllers;

import com.vm.service.QuartzJobManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quartz")
public class QuartzJobController {
    
    @Autowired
    private QuartzJobManager quartzJobManager;
    
    @PostMapping("/schedule-recurring")
    public ResponseEntity<String> scheduleRecurringJob(
            @RequestParam String jobName,
            @RequestParam String cronExpression) {
        try {
            quartzJobManager.scheduleRecurringJob(jobName, cronExpression);
            return ResponseEntity.ok("Recurring job scheduled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to schedule job: " + e.getMessage());
        }
    }
    
    @PostMapping("/start-scheduler")
    public ResponseEntity<String> startScheduler() {
        try {
            quartzJobManager.startScheduler();
            return ResponseEntity.ok("Scheduler started successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to start scheduler: " + e.getMessage());
        }
    }
    
    @PostMapping("/shutdown-scheduler")
    public ResponseEntity<String> shutdownScheduler() {
        try {
            quartzJobManager.shutdownScheduler();
            return ResponseEntity.ok("Scheduler shutdown successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to shutdown scheduler: " + e.getMessage());
        }
    }
}
