-- ------------------------------
-- Table structure for clan_privs
-- ------------------------------
CREATE TABLE IF NOT EXISTS `clan_privs` (
  `clan_id` INT NOT NULL default 0,
  `rank` INT NOT NULL default 0,
  `party` INT NOT NULL default 0,
  `privilleges` INT NOT NULL default 0,
  PRIMARY KEY  (`clan_id`,`rank`,`party`)
);

-- L2Emu Project