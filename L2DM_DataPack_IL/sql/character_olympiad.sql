-- Dark Moon Dev Team
CREATE TABLE `character_olympiad` (
  `char_name` varchar(20) default NULL,
  `char_obj_id` int(11) NOT NULL default '0',
  `char_participate` int(1) default NULL,
  `char_victories` int(3) default NULL,
  `char_defeats` int(3) default NULL,
  `olympiad_points` int(11) default NULL,
  PRIMARY KEY  (`char_obj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
