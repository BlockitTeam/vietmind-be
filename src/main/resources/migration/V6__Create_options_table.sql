-- Change log: V6
-- Description: Create "Options" Table

CREATE TABLE options (
    options_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT,
    option_text TEXT,
    score INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Câu 1: Tôi đang cảm thấy căng thẳng
INSERT INTO options (question_id, option_text, score, created_at, updated_at) VALUES
(1, 'Không đúng với tôi', 0, NOW(), NOW()),
(1, 'Đúng với tôi một phần', 1, NOW(), NOW()),
(1, 'Thỉnh thoảng đúng', 1, NOW(), NOW()),
(1, 'Đúng với tôi', 2, NOW(), NOW()),
(1, 'Luôn luôn đúng', 2, NOW(), NOW());

-- Câu 2: Tôi cảm thấy lo lắng
INSERT INTO options (question_id, option_text, score, created_at, updated_at) VALUES
(2, 'Không đúng với tôi', 0, NOW(), NOW()),
(2, 'Đúng với tôi một phần', 1, NOW(), NOW()),
(2, 'Thỉnh thoảng đúng', 1, NOW(), NOW()),
(2, 'Đúng với tôi', 2, NOW(), NOW()),
(2, 'Luôn luôn đúng', 2, NOW(), NOW());

-- Câu 3: Tôi cảm thấy buồn rầu
INSERT INTO options (question_id, option_text, score, created_at, updated_at) VALUES
(3, 'Không đúng với tôi', 0, NOW(), NOW()),
(3, 'Đúng với tôi một phần', 1, NOW(), NOW()),
(3, 'Thỉnh thoảng đúng', 1, NOW(), NOW()),
(3, 'Đúng với tôi', 2, NOW(), NOW()),
(3, 'Luôn luôn đúng', 2, NOW(), NOW());

-- Câu 4: Tôi thấy người mệt mỏi
INSERT INTO options (question_id, option_text, score, created_at, updated_at) VALUES
(4, 'Không đúng với tôi', 0, NOW(), NOW()),
(4, 'Đúng với tôi một phần', 1, NOW(), NOW()),
(4, 'Thỉnh thoảng đúng', 1, NOW(), NOW()),
(4, 'Đúng với tôi', 2, NOW(), NOW()),
(4, 'Luôn luôn đúng', 2, NOW(), NOW());

-- Câu 5: Tôi bị mất tập trung trong công việc
INSERT INTO options (question_id, option_text, score, created_at, updated_at) VALUES
(5, 'Không đúng với tôi', 0, NOW(), NOW()),
(5, 'Đúng với tôi một phần', 1, NOW(), NOW()),
(5, 'Thỉnh thoảng đúng', 1, NOW(), NOW()),
(5, 'Đúng với tôi', 2, NOW(), NOW()),
(5, 'Luôn luôn đúng', 2, NOW(), NOW());

-- Câu 6: Tôi nghĩ tới cái chết
INSERT INTO options (question_id, option_text, score, created_at, updated_at) VALUES
(6, 'Không đúng với tôi', 0, NOW(), NOW()),
(6, 'Đúng với tôi một phần', 1, NOW(), NOW()),
(6, 'Thỉnh thoảng đúng', 1, NOW(), NOW()),
(6, 'Đúng với tôi', 2, NOW(), NOW()),
(6, 'Luôn luôn đúng', 2, NOW(), NOW());

-- Câu 7: Tôi bị ám ảnh bởi những điều xảy ra trong quá khứ
INSERT INTO options (question_id, option_text, score, created_at, updated_at) VALUES
(7, 'Không đúng với tôi', 0, NOW(), NOW()),
(7, 'Đúng với tôi một phần', 1, NOW(), NOW()),
(7, 'Thỉnh thoảng đúng', 1, NOW(), NOW()),
(7, 'Đúng với tôi', 2, NOW(), NOW()),
(7, 'Luôn luôn đúng', 2, NOW(), NOW());


