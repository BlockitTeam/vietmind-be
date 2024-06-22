-- Change log: V9
-- Description: modify table Conversation remove column

ALTER TABLE conversations DROP COLUMN encrypted_session_key_sender;
ALTER TABLE conversations DROP COLUMN encrypted_session_key_recipient;
TRUNCATE TABLE conversations;