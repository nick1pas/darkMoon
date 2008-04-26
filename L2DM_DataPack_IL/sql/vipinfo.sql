-- Dark Moon Dev Team
CREATE TABLE `vipinfo` (
  `teamID` int(11) NOT NULL default '0',
  `endx` int(11) NOT NULL default '0',
  `endy` int(11) NOT NULL default '0',
  `endz` int(11) NOT NULL default '0',
  `startx` int(11) NOT NULL default '0',
  `starty` int(11) NOT NULL default '0',
  `startz` int(11) NOT NULL default '0',
  PRIMARY KEY  (`teamID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `vipinfo` VALUES
('1', '-84583', '242788', '-3735', '-101319', '213272', '-3100'),
('2', '45714', '49703', '-3065', '55782', '81597', '-3610'),
('3', '11249', '16890', '-4667', '-22732', '12586', '-2996'),
('4', '-44737', '-113582', '-204', '27053', '-88454', '-3286'),
('5', '116047', '-179059', '-1026', '121145', '-215673', '-3571');
