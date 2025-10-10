package com.vm.service;

import com.vm.model.ScheduledJob;

import java.time.LocalDateTime;
import java.util.List;

public interface JobSchedulerService {
    void scheduleAppointmentReminderJobs(String appointmentId, LocalDateTime appointmentDateTime);
    void cancelJobsByEntity(String entityType, String entityId);
    void updateJobsByEntity(String entityType, String entityId, LocalDateTime newDateTime);
    List<ScheduledJob> getJobsByEntity(String entityType, String entityId);
    void retryFailedJobs();
    void markJobAsExecuted(Long jobId);
    void markJobAsFailed(Long jobId, String errorMessage);
    void executeJobById(Long jobId);
}
