-- Shilen's Temple Dev Team
CREATE TABLE `tvt` (
  `eventName` varchar(255) NOT NULL DEFAULT '',
  `eventDesc` varchar(255) NOT NULL DEFAULT '',
  `joiningLocation` varchar(255) NOT NULL DEFAULT '',
  `minlvl` int(4) NOT NULL DEFAULT '1',
  `maxlvl` int(4) NOT NULL DEFAULT '0',
  `npcId` int(8) NOT NULL DEFAULT '0',
  `npcX` int(11) NOT NULL DEFAULT '0',
  `npcY` int(11) NOT NULL DEFAULT '0',
  `npcZ` int(11) NOT NULL DEFAULT '0',
  `npcHeading` int(11) NOT NULL DEFAULT '0',
  `rewardId` int(11) NOT NULL DEFAULT '0',
  `rewardAmount` int(11) NOT NULL DEFAULT '0',
  `teamsCount` int(4) NOT NULL DEFAULT '0',
  `joinTime` int(11) NOT NULL DEFAULT '0',
  `eventTime` int(11) NOT NULL DEFAULT '0',
  `minPlayers` int(4) NOT NULL DEFAULT '1',
  `maxPlayers` int(4) NOT NULL DEFAULT '0',
  `delayForNextEvent` bigint(20) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `tvt` VALUES ('TVT', 'Team Deadmatch', 'Giran Castle Town', '40', '81', '50011', '82688', '148677', '-3469', '0', '57', '1200000', '2', '60', '40', '2', '50', '3600000');
