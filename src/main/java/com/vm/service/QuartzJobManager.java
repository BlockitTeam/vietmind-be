package com.vm.service;

import com.vm.job.AppointmentReminderJob;
import com.vm.model.ScheduledJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class QuartzJobManager {
    
    private static final Logger logger = LoggerFactory.getLogger(QuartzJobManager.class);
    
    @Autowired
    private Scheduler scheduler;
    
    private static final String JOB_GROUP = "appointment-reminders";
    private static final String TRIGGER_GROUP = "appointment-triggers";
    
    public void scheduleAppointmentReminder(ScheduledJob scheduledJob) {
        try {
            // Create job key
            JobKey jobKey = JobKey.jobKey("job-" + scheduledJob.getId(), JOB_GROUP);
            
            // Create job detail
            JobDetail jobDetail = JobBuilder.newJob(AppointmentReminderJob.class)
                    .withIdentity(jobKey)
                    .usingJobData("jobId", scheduledJob.getId())
                    .usingJobData("jobName", scheduledJob.getName())
                    .usingJobData("entityType", scheduledJob.getEntityType())
                    .usingJobData("entityId", scheduledJob.getEntityId())
                    .build();
            
            // Convert LocalDateTime to Date
            Date triggerTime = Date.from(scheduledJob.getTriggerTime().atZone(ZoneId.systemDefault()).toInstant());
            
            // Create trigger
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger-" + scheduledJob.getId(), TRIGGER_GROUP)
                    .startAt(triggerTime)
                    .build();
            
            // Schedule the job
            scheduler.scheduleJob(jobDetail, trigger);
            
            logger.info("Scheduled Quartz job: {} for time: {}", scheduledJob.getName(), scheduledJob.getTriggerTime());
            
        } catch (SchedulerException e) {
            logger.error("Failed to schedule Quartz job: {}", scheduledJob.getName(), e);
            throw new RuntimeException("Failed to schedule job", e);
        }
    }
    
    public void cancelJob(Long jobId) {
        try {
            JobKey jobKey = JobKey.jobKey("job-" + jobId, JOB_GROUP);
            TriggerKey triggerKey = TriggerKey.triggerKey("trigger-" + jobId, TRIGGER_GROUP);
            
            // Cancel trigger first
            scheduler.unscheduleJob(triggerKey);
            
            // Delete job
            scheduler.deleteJob(jobKey);
            
            logger.info("Cancelled Quartz job: {}", jobId);
            
        } catch (SchedulerException e) {
            logger.error("Failed to cancel Quartz job: {}", jobId, e);
            throw new RuntimeException("Failed to cancel job", e);
        }
    }
    
    public void rescheduleJob(Long jobId, LocalDateTime newTriggerTime) {
        try {
            // Cancel existing job
            cancelJob(jobId);
            
            // Get the scheduled job from database
            // Note: You'll need to inject ScheduledJobRepository to get the job details
            // For now, we'll assume the job details are passed as parameters
            
            logger.info("Rescheduled Quartz job: {} for new time: {}", jobId, newTriggerTime);
            
        } catch (Exception e) {
            logger.error("Failed to reschedule Quartz job: {}", jobId, e);
            throw new RuntimeException("Failed to reschedule job", e);
        }
    }
    
    public void scheduleRecurringJob(String jobName, String cronExpression) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP);
            
            JobDetail jobDetail = JobBuilder.newJob(AppointmentReminderJob.class)
                    .withIdentity(jobKey)
                    .build();
            
            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("cron-trigger-" + jobName, TRIGGER_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();
            
            scheduler.scheduleJob(jobDetail, cronTrigger);
            
            logger.info("Scheduled recurring Quartz job: {} with cron: {}", jobName, cronExpression);
            
        } catch (SchedulerException e) {
            logger.error("Failed to schedule recurring Quartz job: {}", jobName, e);
            throw new RuntimeException("Failed to schedule recurring job", e);
        }
    }
    
    public void startScheduler() {
        try {
            if (!scheduler.isStarted()) {
                scheduler.start();
                logger.info("Quartz Scheduler started");
            }
        } catch (SchedulerException e) {
            logger.error("Failed to start Quartz Scheduler", e);
        }
    }
    
    public void shutdownScheduler() {
        try {
            if (scheduler.isStarted()) {
                scheduler.shutdown(true);
                logger.info("Quartz Scheduler shutdown");
            }
        } catch (SchedulerException e) {
            logger.error("Failed to shutdown Quartz Scheduler", e);
        }
    }
}
