-- Dark Moon Dev Team
CREATE TABLE `accounts` (
  `login` varchar(45) NOT NULL default '',
  `password` varchar(45) default NULL,
  `lastactive` decimal(20,0) default NULL,
  `access_level` int(11) NOT NULL default '0',
  `lastIP` varchar(20) default NULL,
  PRIMARY KEY  (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

