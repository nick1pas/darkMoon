-- ----------------------------
-- Table structure for accounts
-- ----------------------------
CREATE TABLE IF NOT EXISTS `accounts` (
  `login` varchar(45) NOT NULL default '',
  `password` varchar(45),
  `lastactive` decimal(20,0),
  `access_level` INT NOT NULL default '0', 
  `lastIP` varchar(20),
  PRIMARY KEY  (`login`)
) DEFAULT CHARSET=utf8;

-- L2Emu Project