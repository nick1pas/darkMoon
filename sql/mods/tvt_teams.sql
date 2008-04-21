-- -----------------------------
-- Table structure for tvt_teams
-- -----------------------------
DROP TABLE IF EXISTS `tvt_teams`;
CREATE TABLE `tvt_teams` (
  `teamId` int(4) NOT NULL default '0',
  `teamName` varchar(255) NOT NULL default '',
  `teamX` int(11) NOT NULL default '0',
  `teamY` int(11) NOT NULL default '0',
  `teamZ` int(11) NOT NULL default '0',
  `teamColor` int(11) NOT NULL default '0',
  PRIMARY KEY (`teamId`)
) DEFAULT CHARSET=utf8;
-- ---------------------------
-- Records for table tvt_teams
-- ---------------------------

INSERT INTO tvt_teams VALUES
(0, 'Team 1', 148391, 46709, -3413, 0), 
(1, 'Team 2', 150639, 46726, -3411, 255);

-- L2Emu Project