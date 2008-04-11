-- ----------------------------------
-- Table structure for faction_quests
-- ----------------------------------
CREATE TABLE IF NOT EXISTS `faction_quests` (
 `id` int(11) NOT NULL default '0',
 `faction_id` int(11) NOT NULL default '0',
 `name` varchar(50) NOT NULL default '0',
 `description` varchar(255) NOT NULL default '0',
 `reward` int NOT NULL default '0',
 `mobid` int(5) NOT NULL default '0',
 `amount` int NOT NULL default '0',
 `min_level` int NOT NULL default '0',
 `max_level` int NOT NULL default '0',
 PRIMARY KEY (`id`)
)DEFAULT CHARSET=utf8;

-- L2Emu Project