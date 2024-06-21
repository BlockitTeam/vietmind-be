-- Change log: V8
-- Description: modify table Conversation -> change user id type

ALTER TABLE conversations MODIFY COLUMN user_id BINARY(16);
ALTER TABLE conversations MODIFY COLUMN doctor_id BINARY(16);
