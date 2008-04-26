-- Dark Moon Dev Team
CREATE TABLE `castle` (
  `id` int(11) NOT NULL default '0',
  `name` varchar(25) NOT NULL default '',
  `taxPercent` int(11) NOT NULL default '15',
  `treasury` int(11) NOT NULL default '0',
  `siegeDate` decimal(20,0) NOT NULL default '0',
  `siegeDayOfWeek` int(11) NOT NULL default '7',
  `siegeHourOfDay` int(11) NOT NULL default '20',
  PRIMARY KEY  (`name`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `castle` VALUES
('5', 'Aden', '0', '0', '0', '7', '20'),
('2', 'Dion', '0', '0', '0', '7', '20'),
('3', 'Giran', '0', '0', '0', '1', '16'),
('1', 'Gludio', '0', '0', '0', '7', '20'),
('7', 'Goddard', '0', '0', '0', '1', '16'),
('6', 'Innadril', '0', '0', '0', '1', '16'),
('4', 'Oren', '0', '0', '0', '1', '16'),
('8', 'Rune', '0', '0', '0', '7', '20'),
('9', 'Schuttgart', '0', '0', '0', '7', '20');
