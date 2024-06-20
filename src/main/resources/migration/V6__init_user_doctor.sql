-- Change log: V6
-- Description: init user doctor

INSERT INTO users
(user_id, birth_year, enabled, first_name, gender, last_name, password, provider, public_key, survey_completed, username)
VALUES(UUID_TO_BIN(UUID()), 0, 1, 'Strange', 'MALE', 'Doctor', '$2a$10$EuW/bSf3U5LJowniJSQHgeg/Tum4014xK67Ka80TMLSa5Cg0Vojky', 'LOCAL', '', 0, 'doctor01@gmail.com');


INSERT INTO users
(user_id, birth_year, enabled, first_name, gender, last_name, password, provider, public_key, survey_completed, username)
VALUES(UUID_TO_BIN(UUID()), 0, 1, 'Chang', 'MALE', 'Doctor', '$2a$10$yHNPMwh0DyxkBMRkDugrGeRVKCx5bLKFlb/zWCgVicACPNqDKhkIq', 'LOCAL', '', 0, 'doctor02@gmail.com');