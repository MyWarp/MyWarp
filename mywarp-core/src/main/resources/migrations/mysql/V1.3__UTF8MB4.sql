-- -----------------------------------------------------
-- Table `${schema}`.`warp`
-- -----------------------------------------------------
ALTER TABLE `${schema}`.`warp`
  MODIFY `name` VARCHAR(32)
    CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_bin' NOT NULL,
  MODIFY `welcome_message` TEXT -- Was TINYTEXT before, but UTF8MB4 may require mote bytes than TINYTEXT allows.
    CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL;
-- -----------------------------------------------------
-- Table `${schema}`.`group`
-- -----------------------------------------------------
ALTER TABLE `${schema}`.`group`
  MODIFY `name` VARCHAR(32)
    CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_bin' NOT NULL;
