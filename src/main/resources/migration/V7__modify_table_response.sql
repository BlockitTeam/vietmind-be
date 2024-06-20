-- Change log: V7
-- Description: modify table Response -> change user id type

ALTER TABLE responses MODIFY COLUMN user_id BINARY(16);
