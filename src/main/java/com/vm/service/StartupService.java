package com.vm.service;

import com.vm.enums.JobStatus;
import com.vm.model.ScheduledJob;
import com.vm.repo.ScheduledJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StartupService {
    
    private static final Logger logger = LoggerFactory.getLogger(StartupService.class);
    
    @Autowired
    private ScheduledJobRepository scheduledJobRepository;
    
    @Autowired
    private QuartzJobManager quartzJobManager;
    
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationReady() {
        logger.info("Application started, loading jobs from database...");
        try {
            // Load all pending and scheduled jobs from database
            List<ScheduledJob> pendingJobs = scheduledJobRepository.findByStatus(JobStatus.PENDING);
            List<ScheduledJob> scheduledJobs = scheduledJobRepository.findByStatus(JobStatus.SCHEDULED);
            
            List<ScheduledJob> allJobs = new ArrayList<>();
            allJobs.addAll(pendingJobs);
            allJobs.addAll(scheduledJobs);
            
            logger.info("Loading {} jobs from database", allJobs.size());
            
            for (ScheduledJob job : allJobs) {
                try {
                    // Schedule job with Quartz
                    quartzJobManager.scheduleAppointmentReminder(job);
                    
                    // Update status to SCHEDULED
                    job.setStatus(JobStatus.SCHEDULED);
                    job.setUpdatedAt(Instant.now());
                    scheduledJobRepository.save(job);
                    
                    logger.info("Loaded job: {} for time: {}", job.getName(), job.getTriggerTime());
                    
                } catch (Exception e) {
                    logger.error("Failed to load job: {}", job.getId(), e);
                    // Mark as failed if can't schedule
                    job.setStatus(JobStatus.FAILED);
                    job.setErrorMessage("Failed to load from database: " + e.getMessage());
                    job.setUpdatedAt(Instant.now());
                    scheduledJobRepository.save(job);
                }
            }
            
            logger.info("Successfully loaded {} jobs from database", allJobs.size());
            
        } catch (Exception e) {
            logger.error("Failed to load jobs from database on startup", e);
        }
    }
}
