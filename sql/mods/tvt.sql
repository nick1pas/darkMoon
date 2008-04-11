-- -----------------------
-- Table structure for tvt
-- -----------------------
DROP TABLE IF EXISTS `tvt`;
CREATE TABLE `tvt` (
  `eventName` varchar(255) NOT NULL default '',
  `eventDesc` varchar(255) NOT NULL default '',
  `joiningLocation` varchar(255) NOT NULL default '',
  `minlvl` int(4) NOT NULL default '1',
  `maxlvl` int(4) NOT NULL default '0',
  `npcId` int(8) NOT NULL default '0',
  `npcX` int(11) NOT NULL default '0',
  `npcY` int(11) NOT NULL default '0',
  `npcZ` int(11) NOT NULL default '0',
  `npcHeading` int(11) NOT NULL default '0',
  `rewardId` int(11) NOT NULL default '0',
  `rewardAmount` int(11) NOT NULL default '0',
  `teamsCount` int(4) NOT NULL default '0',
  `joinTime` int(11) NOT NULL default '0',
  `eventTime` int(11) NOT NULL default '0',
  `minPlayers` int(4) NOT NULL default '1',
  `maxPlayers` int(4) NOT NULL default '0',
  `delayForNextEvent` BIGINT NOT NULL default '0'
) DEFAULT CHARSET=utf8;
-- ---------------------
-- Records for table tvt
-- ---------------------

INSERT INTO  `tvt` values 
('TVT', 'A PvP Event', 'Giran', 1, 81, 50010, 82688, 148677, -3469, 0, 57, 100000, 2, 5, 10, 2, 50, 1800000);

-- L2Emu Project