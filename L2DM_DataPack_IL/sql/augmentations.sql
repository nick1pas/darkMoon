-- Dark Moon Dev Team
CREATE TABLE `augmentations` (
  `item_id` int(11) NOT NULL default '0',
  `attributes` int(11) default '0',
  `skill` int(11) default '0',
  `level` int(11) default '0',
  PRIMARY KEY  (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
