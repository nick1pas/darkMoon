-- Shilen's Temple Dev Team
CREATE TABLE `character_raidpoints` (
  `owner_id` int(11) unsigned NOT NULL default '0',
  `boss_id` int(11) unsigned NOT NULL default '0',
  `points` int(11) NOT NULL default '0',
  PRIMARY KEY  (`owner_id`,`boss_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

