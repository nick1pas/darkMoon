-- -------------------------------------
-- Table structure for character_friends
-- -------------------------------------
CREATE TABLE IF NOT EXISTS `character_friends` (
  `char_id` int(11) NOT NULL default '0',
  `friend_id` int(11) NOT NULL default '0',
  `friend_name` varchar(35) NOT NULL default '',
  PRIMARY KEY  (`char_id`,`friend_name`)
) DEFAULT CHARSET=utf8;

-- L2Emu Project