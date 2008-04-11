-- ---------------------------
-- Table structure for couples
-- ---------------------------
CREATE TABLE IF NOT EXISTS `couples` (
  `id` int(11) NOT NULL auto_increment,
  `player1Id` int(11) NOT NULL,
  `player2Id` int(11) NOT NULL,
  `maried` varchar(5) default NULL,
  `affiancedDate` decimal(20,0) default '0',
  `weddingDate` decimal(20,0) default '0',
  PRIMARY KEY  (`id`)
) DEFAULT CHARSET=utf8;

-- L2Emu Project