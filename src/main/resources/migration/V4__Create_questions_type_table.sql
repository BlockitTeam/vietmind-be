-- Change log: V4
-- Description: Create questions type

CREATE TABLE question_type (
    question_type_id INT PRIMARY KEY AUTO_INCREMENT,
    question_type VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

INSERT INTO question_type (question_type, created_at, updated_at) VALUES
('Lo Âu', NOW(), NOW()),
('Trầm Cảm', NOW(), NOW()),
('Stress', NOW(), NOW()),
('PTSD', NOW(), NOW()),
('Giấc ngủ', NOW(), NOW());