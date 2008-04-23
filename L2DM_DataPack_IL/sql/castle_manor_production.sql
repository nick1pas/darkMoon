-- Shilen's Temple Dev Team
CREATE TABLE `castle_manor_production` (
  `castle_id` int(11) NOT NULL default '0',
  `seed_id` int(11) NOT NULL default '0',
  `can_produce` int(11) NOT NULL default '0',
  `start_produce` int(11) NOT NULL default '0',
  `seed_price` int(11) NOT NULL default '0',
  `period` int(11) NOT NULL default '1',
  PRIMARY KEY  (`castle_id`,`seed_id`,`period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
