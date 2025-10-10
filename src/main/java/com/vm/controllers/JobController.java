package com.vm.controllers;

import com.vm.model.ScheduledJob;
import com.vm.service.JobSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    
    @Autowired
    private JobSchedulerService jobSchedulerService;
    
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<ScheduledJob>> getJobsByEntity(
            @PathVariable String entityType, 
            @PathVariable String entityId) {
        List<ScheduledJob> jobs = jobSchedulerService.getJobsByEntity(entityType, entityId);
        return ResponseEntity.ok(jobs);
    }
    
    @PostMapping("/retry-failed")
    public ResponseEntity<String> retryFailedJobs() {
        jobSchedulerService.retryFailedJobs();
        return ResponseEntity.ok("Failed jobs retry initiated");
    }
    
    
}
