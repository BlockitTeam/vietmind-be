package com.vm.model;

import com.vm.enums.JobStatus;
import com.vm.enums.JobType;
import java.time.Instant;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_jobs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduledJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private JobType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobStatus status = JobStatus.PENDING; // PENDING, SCHEDULED, EXECUTED, FAILED, CANCELLED

    @Column(name = "trigger_time", nullable = false)
    private Instant triggerTime;

    @Column(name = "entity_type", nullable = false)
    private String entityType; // e.g., "appointment"

    @Column(name = "entity_id", nullable = false)
    private String entityId; // e.g., appointment ID

    @Column(name = "job_data", columnDefinition = "TEXT")
    private String jobData; // JSON data for job execution

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "max_retries")
    private Integer maxRetries = 3;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}
