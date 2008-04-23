-- Shilen's Temple Dev Team
DROP TABLE IF EXISTS `faction_quests`;
CREATE TABLE `faction_quests` (
  `id` int(11) NOT NULL default '0',
  `faction_id` int(11) NOT NULL default '0',
  `name` varchar(50) NOT NULL default '0',
  `description` varchar(255) NOT NULL default '0',
  `reward` int(11) NOT NULL default '0',
  `mobid` int(5) NOT NULL default '0',
  `amount` int(11) NOT NULL default '0',
  `min_level` int(11) NOT NULL default '0',
  `max_level` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
