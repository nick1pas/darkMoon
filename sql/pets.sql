-- ------------------------
-- Table structure for pets
-- ------------------------
CREATE TABLE IF NOT EXISTS `pets` (
  `item_obj_id` decimal(11,0) NOT NULL default '0',
  `name` varchar(16) default NULL,
  `level` decimal(11,0) default NULL,
  `curHp` decimal(18,0) default NULL,
  `curMp` decimal(18,0) default NULL,
  `exp` decimal(20,0) default NULL,
  `sp` decimal(11,0) default NULL,
  `karma` decimal(11,0) default NULL,
  `pkkills` decimal(11,0) default NULL,
  `fed` decimal(11,0) default NULL,
  PRIMARY KEY  (`item_obj_id`)
) DEFAULT CHARSET=utf8;

-- L2Emu Project