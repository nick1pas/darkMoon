-- -------------------------
-- Table structure for items
-- -------------------------
CREATE TABLE IF NOT EXISTS `items` (
  `owner_id` int(11) default NULL,
  `object_id` int(11) NOT NULL default '0',
  `item_id` int(11) default NULL,
  `count` int(11) default NULL,
  `enchant_level` int(11) default NULL,
  `loc` varchar(10) default NULL,
  `loc_data` int(11) default NULL,
  `price_sell` int(11) default NULL,
  `price_buy` int(11) default NULL,
  `time_of_use` int(11) default NULL,
  `custom_type1` int(11) default '0',
  `custom_type2` int(11) default '0',
  `mana_left` decimal(3,0) NOT NULL default -1,
  PRIMARY KEY  (`object_id`),
  KEY `key_owner_id` (`owner_id`),
  KEY `key_loc` (`loc`),
  KEY `key_item_id` (`item_id`),
  KEY `key_time_of_use` (`time_of_use`)
) DEFAULT CHARSET=utf8;

-- L2Emu Project