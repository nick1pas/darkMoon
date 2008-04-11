-- --------------------------
-- Table structure for heroes
-- --------------------------
CREATE TABLE IF NOT EXISTS `heroes` (
  `char_id` decimal(11,0) NOT NULL default '0',
  `char_name` varchar(45) NOT NULL default '',
  `class_id` decimal(3,0) NOT NULL default '0',
  `count` decimal(3,0) NOT NULL default '0',
  `played` decimal(1,0) NOT NULL default '0',
  `donator` decimal(1,0) NOT NULL default '0',
  PRIMARY KEY  (`char_id`)
) DEFAULT CHARSET=utf8;

-- L2Emu Project