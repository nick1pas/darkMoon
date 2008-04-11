-- ----------------------------------------
-- Table structure for character_raidpoints
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `character_raidpoints` (
  `owner_id` int(11) unsigned NOT NULL,
  `boss_id` int(11) unsigned NOT NULL,
  `points` int(11) NOT NULL default '0',
  PRIMARY KEY(`owner_id`, `boss_id`)
) DEFAULT CHARSET=utf8;

-- L2Emu Project