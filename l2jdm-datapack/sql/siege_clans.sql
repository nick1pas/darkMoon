CREATE TABLE IF NOT EXISTS `siege_clans` (
  `castle_id` INT(1) NOT NULL DEFAULT 0,
  `clan_id` INT(11) NOT NULL DEFAULT 0,
  `type` INT(1) DEFAULT NULL,
  `castle_owner` INT(1) DEFAULT NULL,
  PRIMARY KEY (`clan_id`,`castle_id`)
) DEFAULT CHARSET=utf8;