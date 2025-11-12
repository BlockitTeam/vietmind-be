-- Create scheduled_jobs table for job scheduling system
CREATE TABLE scheduled_jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    trigger_time DATETIME NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    job_data TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    executed_at DATETIME,
    error_message TEXT,
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    
    INDEX idx_status_trigger_time (status, trigger_time),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_name (name),
    INDEX idx_trigger_time (trigger_time)
);

-- Add device_token field to users table for push notifications
ALTER TABLE users ADD COLUMN device_token VARCHAR(500) NULL;
