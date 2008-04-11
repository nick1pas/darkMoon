-- ----------------------------------------
-- Table structure for character_recipebook
-- ----------------------------------------
CREATE TABLE IF NOT EXISTS `character_recipebook` (
  `char_id` decimal(11,0) NOT NULL default '0',
  `id` decimal(11,0) NOT NULL default '0',
  `type` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`,`char_id`)
) DEFAULT CHARSET=utf8;

-- L2Emu Project