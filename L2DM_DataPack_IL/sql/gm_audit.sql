-- Shilen's Temple Dev Team
CREATE TABLE `gm_audit` (
  `id` int(10) NOT NULL auto_increment,
  `gm_name` varchar(45) default NULL,
  `target` varchar(45) default NULL,
  `type` varchar(20) default NULL,
  `action` varchar(200) default NULL,
  `param` varchar(200) default NULL,
  `date` date default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
