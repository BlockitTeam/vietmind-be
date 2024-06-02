-- Change log: V5
-- Description: Create questions

INSERT INTO questions (idx, survey_id, question_text, question_type_id, created_at, updated_at) VALUES
(1, 1, 'Tôi đang cảm thấy căng thẳng', (SELECT question_type_id FROM question_type WHERE question_type = 'Stress'), NOW(), NOW()),
(2, 1, 'Tôi cảm thấy lo lắng', (SELECT question_type_id FROM question_type WHERE question_type = 'Lo Âu'), NOW(), NOW()),
(3, 1, 'Tôi cảm thấy buồn rầu', (SELECT question_type_id FROM question_type WHERE question_type = 'Trầm Cảm'), NOW(), NOW()),
(4, 1, 'Tôi thấy người mệt mỏi', (SELECT question_type_id FROM question_type WHERE question_type = 'Trầm Cảm'), NOW(), NOW()),
(5, 1, 'Tôi bị mất tập trung trong công việc', (SELECT question_type_id FROM question_type WHERE question_type = 'Trầm Cảm'), NOW(), NOW()),
(6, 1, 'Tôi nghĩ tới cái chết', (SELECT question_type_id FROM question_type WHERE question_type = 'PTSD'), NOW(), NOW()),
(7, 1, 'Tôi bị ám ảnh bởi những điều xảy ra trong quá khứ', (SELECT question_type_id FROM question_type WHERE question_type = 'Giấc ngủ'), NOW(), NOW());