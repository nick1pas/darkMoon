-- Shilen's Temple Dev Team
CREATE TABLE `character_friends` (
  `char_id` int(11) NOT NULL default '0',
  `friend_id` int(11) NOT NULL default '0',
  `friend_name` varchar(35) NOT NULL default '',
  PRIMARY KEY  (`char_id`,`friend_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
