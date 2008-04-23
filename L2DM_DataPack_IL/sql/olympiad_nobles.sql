-- Shilen's Temple Dev Team
CREATE TABLE `olympiad_nobles` (
  `char_id` decimal(11,0) NOT NULL default '0',
  `class_id` decimal(3,0) NOT NULL default '0',
  `char_name` varchar(45) NOT NULL default '',
  `olympiad_points` decimal(10,0) NOT NULL default '0',
  `competitions_done` decimal(3,0) NOT NULL default '0',
  PRIMARY KEY  (`char_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
