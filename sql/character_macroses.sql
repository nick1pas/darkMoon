-- --------------------------------------
-- Table structure for character_macroses
-- --------------------------------------
CREATE TABLE IF NOT EXISTS `character_macroses` (
  `char_obj_id` int(11) NOT NULL default '0',
  `id` int(11) NOT NULL default '0',
  `icon` int(11) default NULL,
  `name` varchar(40) default NULL,
  `descr` varchar(80) default NULL,
  `acronym` varchar(4) default NULL,
  `commands` varchar(255) default NULL,
  PRIMARY KEY  (`char_obj_id`,`id`)
) DEFAULT CHARSET=utf8;

-- L2Emu Project
