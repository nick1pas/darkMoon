-- Shilen's Temple Dev Team
CREATE TABLE `ctf_teams` (
  `teamId` int(4) NOT NULL default '0',
  `teamName` varchar(255) NOT NULL default '',
  `teamX` int(11) NOT NULL default '0',
  `teamY` int(11) NOT NULL default '0',
  `teamZ` int(11) NOT NULL default '0',
  `teamColor` int(11) NOT NULL default '0',
  `flagX` int(11) NOT NULL default '0',
  `flagY` int(11) NOT NULL default '0',
  `flagZ` int(11) NOT NULL default '0',
  PRIMARY KEY  (`teamId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
