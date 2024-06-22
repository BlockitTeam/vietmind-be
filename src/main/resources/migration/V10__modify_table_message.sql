-- Change log: V10
-- Description: modify table Messages -> change user id type

TRUNCATE TABLE messages;
ALTER TABLE messages MODIFY COLUMN sender_id BINARY(16);
ALTER TABLE messages MODIFY COLUMN receiver_id BINARY(16);
