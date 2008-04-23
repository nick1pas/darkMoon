-- Shilen's Temple Dev Team
CREATE TABLE `account_data` (
  `account_name` varchar(45) NOT NULL default '',
  `var` varchar(20) NOT NULL default '',
  `value` varchar(255) default NULL,
  PRIMARY KEY  (`account_name`,`var`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
