-- -----------------------------
-- Table structure for clan_data
-- -----------------------------
CREATE TABLE IF NOT EXISTS `clan_data` (
  `clan_id` int(11) NOT NULL default 0,
  `clan_name` varchar(45) default NULL,
  `clan_level` int(11) default NULL,
  `hasCastle` int(11) default NULL,
  `ally_id` int(11) default NULL,
  `ally_name` varchar(45) default NULL,
  `leader_id` int(11) default NULL,
  `crest_id` int(11) default NULL,
  `crest_large_id` int(11) default NULL,
  `ally_crest_id` int(11) default NULL,
  `reputation_score` INT NOT NULL default 0,
  `rank` INT NOT NULL default 0,
  `auction_bid_at` int(11) NOT NULL DEFAULT 0,
  `ally_penalty_expiry_time` DECIMAL(20,0) NOT NULL DEFAULT 0,
  `ally_penalty_type` DECIMAL(1) NOT NULL DEFAULT 0,
  `char_penalty_expiry_time` DECIMAL(20,0) NOT NULL DEFAULT 0,
  `dissolving_expiry_time` DECIMAL(20,0) NOT NULL DEFAULT 0,
  PRIMARY KEY  (`clan_id`),
  KEY `leader_id` (`leader_id`),
  KEY `ally_id` (`ally_id`)
) DEFAULT CHARSET=utf8;

-- L2Emu Project