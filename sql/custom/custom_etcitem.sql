-- ----------------------------------
-- Table structure for custom_etcitem
-- ----------------------------------
DROP TABLE IF EXISTS `custom_etcitem`;
CREATE TABLE `custom_etcitem` (
  `item_id` decimal(11,0) NOT NULL default '0',
  `item_display_id` decimal(11,0) NOT NULL default '0',
  `name` varchar(100) default NULL,
  `crystallizable` varchar(5) default NULL,
  `item_type` varchar(14) default NULL,
  `weight` decimal(4,0) default NULL,
  `consume_type` varchar(9) default NULL,
  `material` varchar(11) default NULL,
  `crystal_type` varchar(4) default NULL,
  `duration` decimal(3,0) default NULL,
  `price` decimal(11,0) default NULL,
  `crystal_count` int(4) default NULL,
  `sellable` varchar(5) default NULL,
  `dropable` varchar(5) default NULL,
  `destroyable` varchar(5) default NULL,
  `tradeable` varchar(5) default NULL,
  `oldname` varchar(100) NOT NULL default '',
  `oldtype` varchar(100) NOT NULL default '',
  PRIMARY KEY (`item_id`)
) DEFAULT CHARSET=utf8;

--
-- L2Emu Changes
--

INSERT INTO `custom_etcitem` VALUES 
(15000, 15000, 'Gold Coin', 'false', 'material', '0', 'stackable', 'steel', 'none', '100', '0', '0', 'false', 'false', 'false', 'true', 'q_coin_diagram', 'none');

-- L2Emu Project