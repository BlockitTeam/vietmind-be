-- Change log: V1
-- Description: Create and insert table surveys

CREATE TABLE IF NOT EXISTS surveys (
    survey_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO surveys (title, description, created_at, updated_at) VALUES
('Sàng lọc chung', 'Qui trình tham vấn', NOW(), NOW());

-- table Question
CREATE TABLE IF NOT EXISTS questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    survey_id INT,
    question_text TEXT,
    question_type_id INT,
    idx INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- table Question Type
CREATE TABLE IF NOT EXISTS question_type (
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