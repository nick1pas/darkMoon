-- ---------------------------
-- Table structure for vipinfo
-- ---------------------------
CREATE TABLE IF NOT EXISTS `vipinfo` (
  `teamID` int(11) NOT NULL,
  `endx` int(11) NOT NULL,
  `endy` int(11) NOT NULL,
  `endz` int(11) NOT NULL,
  `startx` int(11) NOT NULL,
  `starty` int(11) NOT NULL,
  `startz` int(11) NOT NULL,
  PRIMARY KEY  (`teamID`)
) DEFAULT CHARSET=utf8;
-- -------------------------
-- Records for table vipinfo
-- -------------------------

insert ignore into vipinfo values
(1, -84583, 242788, -3735, -101319, 213272, -3100), 
(2, 45714, 49703, -3065, 55782, 81597, -3610), 
(3, 11249, 16890, -4667, -22732, 12586, -2996), 
(4, -44737, -113582, -204, 27053, -88454, -3286), 
(5, 116047, -179059, -1026, 121145, -215673, -3571);

-- L2Emu Project