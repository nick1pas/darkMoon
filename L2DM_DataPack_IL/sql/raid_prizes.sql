-- Shilen's Temple Dev Team
DROP TABLE IF EXISTS `raid_prizes`;
CREATE TABLE `raid_prizes` (
  `prize_package_id` int(11) NOT NULL default '0',
  `first_prize_id` int(11) NOT NULL default '0',
  `first_prize_ammount` int(11) NOT NULL default '0',
  `second_prize_id` int(11) NOT NULL default '0',
  `second_prize_ammount` int(11) NOT NULL default '0',
  `event_points_ammount` int(11) NOT NULL default '0',
  PRIMARY KEY  (`prize_package_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `raid_prizes` VALUES 
('1', '3470', '15', '6393', '20', '3'),
('2', '3470', '25', '6393', '35', '4'),
('3', '3470', '40', '6393', '40', '5'),
('4', '3470', '50', '6393', '50', '10'),
('5', '3470', '75', '6393', '75', '15'),
('6', '3470', '150', '6393', '150', '20'),
('7', '3470', '3', '6393', '10', '1'),
('8', '3470', '6', '6393', '15', '2'),
('9', '3470', '8', '6393', '18', '2'),
('10', '3470', '20', '6393', '30', '3'),
('11', '3470', '30', '6393', '40', '5');
