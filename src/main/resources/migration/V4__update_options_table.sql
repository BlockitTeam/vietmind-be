-- Change log: V4
-- Description: Update "Options" Table

ALTER TABLE `options`
DROP COLUMN `option_id`,
CHANGE COLUMN `options_id` `option_id` INT NOT NULL AUTO_INCREMENT ;