-- Change log: V3
-- Description: Create and insert table surveys

CREATE TABLE surveys (
    survey_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO surveys (title, description, created_at, updated_at) VALUES
('Sàng lọc chung', 'Qui trình tham vấn', NOW(), NOW());