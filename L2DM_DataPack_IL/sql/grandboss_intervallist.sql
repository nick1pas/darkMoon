-- Dark Moon Dev Team
DROP TABLE IF EXISTS `grandboss_intervallist`;
CREATE TABLE `grandboss_intervallist` (
  `bossId` int(11) NOT NULL default '0',
  `respawnDate` decimal(20,0) NOT NULL default '0',
  `state` int(11) NOT NULL default '0',
  PRIMARY KEY  (`bossId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `grandboss_intervallist` VALUES 
('29019', '0', '0'),
('29020', '0', '0'),
('29028', '0', '0'),
('29062', '0', '0'),
('29065', '0', '0');
