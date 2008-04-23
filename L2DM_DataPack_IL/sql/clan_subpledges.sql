-- Shilen's Temple Dev Team
CREATE TABLE `clan_subpledges` (
  `clan_id` int(11) NOT NULL default '0',
  `sub_pledge_id` int(11) NOT NULL default '0',
  `name` varchar(45) default NULL,
  `leader_name` varchar(35) default NULL,
  PRIMARY KEY  (`clan_id`,`sub_pledge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

