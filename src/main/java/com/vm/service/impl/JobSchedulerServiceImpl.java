package com.vm.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vm.enums.JobStatus;
import com.vm.enums.JobType;
import com.vm.model.Appointment;
import com.vm.model.ScheduledJob;
import com.vm.model.User;
import com.vm.repo.AppointmentRepository;
import com.vm.repo.ScheduledJobRepository;
import com.vm.repo.UserRepository;
import com.vm.service.EmailService;
import com.vm.service.JobSchedulerService;
import com.vm.service.PushNotificationService;
import com.vm.service.QuartzJobManager;
import java.time.Instant;
import java.time.ZoneOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JobSchedulerServiceImpl implements JobSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(JobSchedulerServiceImpl.class);

    @Autowired
    private ScheduledJobRepository scheduledJobRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private QuartzJobManager quartzJobManager;

    // Job type constants - now using enums
    public static final JobType APPOINTMENT_MAIL_24H_REMINDER = JobType.APPOINTMENT_MAIL_24H_REMINDER;
    public static final JobType APPOINTMENT_NOTIFICATION_1H = JobType.APPOINTMENT_NOTIFICATION_1H;

    @Override
    @Transactional
    public void scheduleAppointmentReminderJobs(String appointmentId,
            LocalDateTime appointmentDateTime) {
        try {
            // Cancel existing jobs for this appointment
            cancelJobsByEntity("appointment", appointmentId);

            // Schedule 24h email reminder
            LocalDateTime emailReminderTime = appointmentDateTime.minusHours(24);
            if (!emailReminderTime.isAfter(LocalDateTime.now())) {
                createJob(
                        "appointment_mail_24h_" + appointmentId,
                        APPOINTMENT_MAIL_24H_REMINDER,
                        Instant.now().plusSeconds(30),
                        "appointment",
                        appointmentId,
                        createJobData(24, "email")
                );
            }

            // Schedule 1h push notification
            LocalDateTime notificationReminderTime = appointmentDateTime.minusHours(1);
            if (notificationReminderTime.isAfter(LocalDateTime.now())) {
                createJob(
                        "appointment_notification_1h_" + appointmentId,
                        APPOINTMENT_NOTIFICATION_1H,
                        notificationReminderTime.toInstant(ZoneOffset.of("+07:00")),
                        "appointment",
                        appointmentId,
                        createJobData(1, "notification")
                );
            }

            logger.info("Scheduled reminder jobs for appointment: {}", appointmentId);

        } catch (Exception e) {
            logger.error("Failed to schedule reminder jobs for appointment: {}", appointmentId, e);
            throw new RuntimeException("Failed to schedule reminder jobs", e);
        }
    }

    @Override
    @Transactional
    public void cancelJobsByEntity(String entityType, String entityId) {
        List<ScheduledJob> activeJobs = scheduledJobRepository.findActiveJobsByEntity(entityType,
                entityId);
        for (ScheduledJob job : activeJobs) {
            // Cancel Quartz job
            quartzJobManager.cancelJob(job.getId());
            
            job.setStatus(JobStatus.CANCELLED);
            scheduledJobRepository.save(job);
        }
        logger.info("Cancelled {} jobs for entity: {} - {}", activeJobs.size(), entityType,
                entityId);
    }

    @Override
    @Transactional
    public void updateJobsByEntity(String entityType, String entityId, LocalDateTime newDateTime) {
        // Cancel existing jobs
        cancelJobsByEntity(entityType, entityId);

        // Schedule new jobs with updated time
        if (entityType.equals("appointment")) {
            scheduleAppointmentReminderJobs(entityId, newDateTime);
        }
    }


    @Override
    public List<ScheduledJob> getJobsByEntity(String entityType, String entityId) {
        return scheduledJobRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    @Override
    @Transactional
    public void retryFailedJobs() {
        List<ScheduledJob> failedJobs = scheduledJobRepository.findByStatusAndTriggerTimeLessThanEqual(
                JobStatus.FAILED, LocalDateTime.now());

        for (ScheduledJob job : failedJobs) {
            if (job.getRetryCount() < job.getMaxRetries()) {
                job.setStatus(JobStatus.PENDING);
                job.setRetryCount(job.getRetryCount() + 1);
                job.setErrorMessage(null);
                scheduledJobRepository.save(job);

                logger.info("Retrying job: {} (attempt {})", job.getId(), job.getRetryCount());
            }
        }
    }

    @Override
    @Transactional
    public void markJobAsExecuted(Long jobId) {
        Optional<ScheduledJob> jobOpt = scheduledJobRepository.findById(jobId);
        if (jobOpt.isPresent()) {
            ScheduledJob job = jobOpt.get();
            job.setStatus(JobStatus.EXECUTED);
            job.setExecutedAt(LocalDateTime.now());
            scheduledJobRepository.save(job);
        }
    }

    @Override
    @Transactional
    public void markJobAsFailed(Long jobId, String errorMessage) {
        Optional<ScheduledJob> jobOpt = scheduledJobRepository.findById(jobId);
        if (jobOpt.isPresent()) {
            ScheduledJob job = jobOpt.get();
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(errorMessage);
            scheduledJobRepository.save(job);
        }
    }

    private void createJob(String jobName, JobType jobType, Instant triggerTime,
            String entityType, String entityId, Map<String, Object> jobData) {
        try {
            ScheduledJob job = ScheduledJob.builder()
                    .name(jobName)
                    .type(jobType)
                    .status(JobStatus.PENDING)
                    .triggerTime(triggerTime)
                    .entityType(entityType)
                    .entityId(entityId)
                    .jobData(objectMapper.writeValueAsString(jobData))
                    .build();

            ScheduledJob savedJob = scheduledJobRepository.save(job);
            
            // Schedule with Quartz
            quartzJobManager.scheduleAppointmentReminder(savedJob);
            
            logger.info("Created job: {} for entity: {} - {}", jobName, entityType, entityId);

        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize job data", e);
            throw new RuntimeException("Failed to create job", e);
        }
    }

    private Map<String, Object> createJobData(int hoursBefore, String notificationType) {
        Map<String, Object> data = new HashMap<>();
        data.put("hoursBefore", hoursBefore);
        data.put("notificationType", notificationType);
        return data;
    }

    private void executeJob(ScheduledJob job) throws Exception {
        logger.info("Executing job: {} of type: {}", job.getName(), job.getType());

        switch (job.getType()) {
            case APPOINTMENT_MAIL_24H_REMINDER:
                executeAppointmentEmailReminder(job);
                break;
            case APPOINTMENT_NOTIFICATION_1H:
                executeAppointmentNotificationReminder(job);
                break;
            default:
                logger.warn("Unknown job type: {}", job.getType());
        }
    }

    private void executeAppointmentEmailReminder(ScheduledJob job) throws Exception {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(
                Integer.parseInt(job.getEntityId()));
        if (appointmentOpt.isEmpty()) {
            throw new Exception("Appointment not found: " + job.getEntityId());
        }

        Appointment appointment = appointmentOpt.get();
        Optional<User> userOpt = userRepository.findById(
                java.util.UUID.fromString(appointment.getUserId()));
        if (userOpt.isEmpty()) {
            throw new Exception("User not found: " + appointment.getUserId());
        }

        User user = userOpt.get();
        emailService.sendAppointmentReminderEmail(user, appointment, 24);
    }

    private void executeAppointmentNotificationReminder(ScheduledJob job) throws Exception {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(
                Integer.parseInt(job.getEntityId()));
        if (appointmentOpt.isEmpty()) {
            throw new Exception("Appointment not found: " + job.getEntityId());
        }

        Appointment appointment = appointmentOpt.get();
        Optional<User> userOpt = userRepository.findById(
                java.util.UUID.fromString(appointment.getUserId()));
        if (userOpt.isEmpty()) {
            throw new Exception("User not found: " + appointment.getUserId());
        }

        User user = userOpt.get();
        pushNotificationService.sendAppointmentReminderNotification(user, appointment, 1);
    }
    
    @Override
    @Transactional
    public void executeJobById(Long jobId) {
        Optional<ScheduledJob> jobOpt = scheduledJobRepository.findById(jobId);
        if (jobOpt.isPresent()) {
            ScheduledJob job = jobOpt.get();
            try {
                executeJob(job);
                markJobAsExecuted(jobId);
            } catch (Exception e) {
                logger.error("Failed to execute job: {}", jobId, e);
                markJobAsFailed(jobId, e.getMessage());
            }
        } else {
            logger.warn("Job not found: {}", jobId);
        }
    }
    
}
