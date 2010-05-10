CREATE TABLE IF NOT EXISTS `character_subclasses` (
  `charId` INT UNSIGNED NOT NULL,
  `class_id` TINYINT UNSIGNED NOT NULL,
  `exp` BIGINT UNSIGNED NOT NULL,
  `sp` INT UNSIGNED NOT NULL,
  `level` TINYINT(2) UNSIGNED NOT NULL,
  `class_index` TINYINT UNSIGNED NOT NULL,
  PRIMARY KEY (`charId`,`class_id`)
) DEFAULT CHARSET=utf8;