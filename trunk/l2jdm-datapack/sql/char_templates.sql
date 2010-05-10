DROP TABLE IF EXISTS `char_templates`;
CREATE TABLE `char_templates` (
  `ClassId` INT(11) NOT NULL DEFAULT 0,
  `ClassName` VARCHAR(20) NOT NULL DEFAULT '',
  `RaceId` INT(1) NOT NULL DEFAULT 0,
  `STR` INT(2) NOT NULL DEFAULT 0,
  `CON` INT(2) NOT NULL DEFAULT 0,
  `DEX` INT(2) NOT NULL DEFAULT 0,
  `_INT` INT(2) NOT NULL DEFAULT 0,
  `WIT` INT(2) NOT NULL DEFAULT 0,
  `MEN` INT(2) NOT NULL DEFAULT 0,
  `P_ATK` INT(3) NOT NULL DEFAULT 0,
  `P_DEF` INT(3) NOT NULL DEFAULT 0,
  `M_ATK` INT(3) NOT NULL DEFAULT 0,
  `M_DEF` INT(2) NOT NULL DEFAULT 0,
  `P_SPD` INT(3) NOT NULL DEFAULT 0,
  `M_SPD` INT(3) NOT NULL DEFAULT 0,
  `ACC` INT(3) NOT NULL DEFAULT 0,
  `CRITICAL` INT(3) NOT NULL DEFAULT 0,
  `EVASION` INT(3) NOT NULL DEFAULT 0,
  `MOVE_SPD` INT(3) NOT NULL DEFAULT 0,
  `_LOAD` INT(11) NOT NULL DEFAULT 0,
  `x` INT(9) NOT NULL DEFAULT 0,
  `y` INT(9) NOT NULL DEFAULT 0,
  `z` INT(9) NOT NULL DEFAULT 0,
  `canCraft` INT(1) NOT NULL DEFAULT 0,
  `M_UNK1` DECIMAL(4,2) NOT NULL DEFAULT '0.00',
  `M_UNK2` DECIMAL(8,6) NOT NULL DEFAULT '0.000000',
  `M_COL_R` DECIMAL(3,1) NOT NULL DEFAULT '0.0',
  `M_COL_H` DECIMAL(4,1) NOT NULL DEFAULT '0.0',
  `F_UNK1` DECIMAL(4,2) NOT NULL DEFAULT '0.00',
  `F_UNK2` DECIMAL(8,6) NOT NULL DEFAULT '0.000000',
  `F_COL_R` DECIMAL(3,1) NOT NULL DEFAULT '0.0',
  `F_COL_H` DECIMAL(4,1) NOT NULL DEFAULT '0.0',
  PRIMARY KEY (`ClassId`)
) DEFAULT CHARSET=utf8;

INSERT INTO `char_templates` VALUES
(0, 'Human Fighter', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(1, 'Warrior', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(2, 'Gladiator', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(3, 'Warlord', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(4, 'Human Knight', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(5, 'Paladin', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(6, 'Dark Avenger', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(7, 'Rogue', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(8, 'Treasure Hunter', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(9, 'Hawkeye', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(10, 'Human Mage', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(11, 'Human Wizard', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(12, 'Sorcerer', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(13, 'Necromancer', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(14, 'Warlock', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(15, 'Cleric', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(16, 'Bishop', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(17, 'Human Prophet', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(18, 'Elf Fighter', 1, 36, 36, 35, 23, 14, 26, 4, 80, 6, 41, 300, 333, 36, 46, 36, 125, 73000, 45978, 41196, -3440, 0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0),
(19, 'Elf Knight', 1, 36, 36, 35, 23, 14, 26, 4, 80, 6, 41, 300, 333, 36, 46, 36, 125, 73000, 45978, 41196, -3440, 0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0),
(20, 'Temple Knight', 1, 36, 36, 35, 23, 14, 26, 4, 80, 6, 41, 300, 333, 36, 46, 36, 125, 73000, 45978, 41196, -3440, 0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0),
(21, 'Swordsinger', 1, 36, 36, 35, 23, 14, 26, 4, 80, 6, 41, 300, 333, 36, 46, 36, 125, 73000, 45978, 41196, -3440, 0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0),
(22, 'Scout', 1, 36, 36, 35, 23, 14, 26, 4, 80, 6, 41, 300, 333, 36, 46, 36, 125, 73000, 45978, 41196, -3440, 0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0),
(23, 'Plains Walker', 1, 36, 36, 35, 23, 14, 26, 4, 80, 6, 41, 300, 333, 36, 46, 36, 125, 73000, 45978, 41196, -3440, 0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0),
(24, 'Silver Ranger', 1, 36, 36, 35, 23, 14, 26, 4, 80, 6, 41, 300, 333, 36, 46, 36, 125, 73000, 45978, 41196, -3440, 0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0),
(25, 'Elf Mage', 1, 21, 25, 24, 37, 23, 40, 3, 54, 6, 41, 300, 333, 30, 41, 30, 122, 62400, 46182, 41198, -3440, 0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0),
(26, 'Elf Wizard', 1, 21, 25, 24, 37, 23, 40, 3, 54, 6, 41, 300, 333, 30, 41, 30, 122, 62400, 46182, 41198, -3440, 0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0),
(27, 'Spellsinger', 1, 21, 25, 24, 37, 23, 40, 3, 54, 6, 41, 300, 333, 30, 41, 30, 122, 62400, 46182, 41198, -3440, 0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0),
(28, 'Elemental Summoner', 1, 21, 25, 24, 37, 23, 40, 3, 54, 6, 41, 300, 333, 30, 41, 30, 122, 62400, 46182, 41198, -3440, 0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0),
(29, 'Oracle', 1, 21, 25, 24, 37, 23, 40, 3, 54, 6, 41, 300, 333, 30, 41, 30, 122, 62400, 46182, 41198, -3440, 0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0),
(30, 'Elder', 1, 21, 25, 24, 37, 23, 40, 3, 54, 6, 41, 300, 333, 30, 41, 30, 122, 62400, 46182, 41198, -3440, 0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0),
(31, 'DE Fighter', 2, 41, 32, 34, 25, 12, 26, 4, 80, 6, 41, 300, 333, 35, 45, 35, 122, 69000, 28377, 10916, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5),
(32, 'Palus Knight', 2, 41, 32, 34, 25, 12, 26, 4, 80, 6, 41, 300, 333, 35, 45, 35, 122, 69000, 28377, 10916, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5),
(33, 'Shillien Knight', 2, 41, 32, 34, 25, 12, 26, 4, 80, 6, 41, 300, 333, 35, 45, 35, 122, 69000, 28377, 10916, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5),
(34, 'Bladedancer', 2, 41, 32, 34, 25, 12, 26, 4, 80, 6, 41, 300, 333, 35, 45, 35, 122, 69000, 28377, 10916, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5),
(35, 'Assassin', 2, 41, 32, 34, 25, 12, 26, 4, 80, 6, 41, 300, 333, 35, 45, 35, 122, 69000, 28377, 10916, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5),
(36, 'Abyss Walker', 2, 41, 32, 34, 25, 12, 26, 4, 80, 6, 41, 300, 333, 35, 45, 35, 122, 69000, 28377, 10916, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5),
(37, 'Phantom Ranger', 2, 41, 32, 34, 25, 12, 26, 4, 80, 6, 41, 300, 333, 35, 45, 35, 122, 69000, 28377, 10916, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5),
(38, 'DE Mage', 2, 23, 24, 23, 44, 19, 37, 3, 54, 6, 41, 300, 333, 29, 41, 29, 122, 61000, 28295, 11063, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5),
(39, 'DE Wizard', 2, 23, 24, 23, 44, 19, 37, 3, 54, 6, 41, 300, 333, 29, 41, 29, 122, 61000, 28295, 11063, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5),
(40, 'Spell Howler', 2, 23, 24, 23, 44, 19, 37, 3, 54, 6, 41, 300, 333, 29, 41, 29, 122, 61000, 28295, 11063, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5),
(41, 'Phantom Summoner', 2, 23, 24, 23, 44, 19, 37, 3, 54, 6, 41, 300, 333, 29, 41, 29, 122, 61000, 28295, 11063, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5),
(42, 'Shillien Oracle', 2, 23, 24, 23, 44, 19, 37, 3, 54, 6, 41, 300, 333, 29, 41, 29, 122, 61000, 28295, 11063, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5),
(43, 'Shillien Elder', 2, 23, 24, 23, 44, 19, 37, 3, 54, 6, 41, 300, 333, 29, 41, 29, 122, 61000, 28295, 11063, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5),
(44, 'Orc Fighter', 3, 40, 47, 26, 18, 12, 27, 4, 80, 6, 41, 300, 333, 31, 42, 31, 117, 87000, -56693, -113610, -690, 0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0),
(45, 'Raider', 3, 40, 47, 26, 18, 12, 27, 4, 80, 6, 41, 300, 333, 31, 42, 31, 117, 87000, -56693, -113610, -690, 0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0),
(46, 'Destroyer', 3, 40, 47, 26, 18, 12, 27, 4, 80, 6, 41, 300, 333, 31, 42, 31, 117, 87000, -56693, -113610, -690, 0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0),
(47, 'Monk', 3, 40, 47, 26, 18, 12, 27, 4, 80, 6, 41, 300, 333, 31, 42, 31, 117, 87000, -56682, -113610, -690, 0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0),
(48, 'Tyrant', 3, 40, 47, 26, 18, 12, 27, 4, 80, 6, 41, 300, 333, 31, 42, 31, 117, 87000, -56693, -113610, -690, 0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0),
(49, 'Orc Mage', 3, 27, 31, 24, 31, 15, 42, 3, 54, 6, 41, 300, 333, 30, 41, 30, 121, 68000, -56682, -113730, -690, 0, 1.04, 0.898560, 7.0, 27.5, 1.04, 0.898560, 8.0, 25.5),
(50, 'Shaman', 3, 27, 31, 24, 31, 15, 42, 3, 54, 6, 41, 300, 333, 30, 41, 30, 121, 68000, -56682, -113730, -690, 0, 1.04, 0.898560, 7.0, 27.5, 1.04, 0.898560, 8.0, 25.5),
(51, 'Overlord', 3, 27, 31, 24, 31, 15, 42, 3, 54, 6, 41, 300, 333, 30, 41, 30, 121, 68000, -56682, -113730, -690, 0, 1.04, 0.898560, 7.0, 27.5, 1.04, 0.898560, 8.0, 25.5),
(52, 'Warcryer', 3, 27, 31, 24, 31, 15, 42, 3, 54, 6, 41, 300, 333, 30, 41, 30, 121, 68000, -56682, -113730, -690, 0, 1.04, 0.898560, 7.0, 27.5, 1.04, 0.898560, 8.0, 25.5),
(53, 'Dwarf Fighter', 4, 39, 45, 29, 20, 10, 27, 4, 80, 6, 41, 300, 333, 33, 43, 33, 115, 83000, 108512, -174026, -400, 1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0),
(54, 'Scavenger', 4, 39, 45, 29, 20, 10, 27, 4, 80, 6, 41, 300, 333, 33, 43, 33, 115, 83000, 108512, -174026, -400, 1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0),
(55, 'Bounty Hunter', 4, 39, 45, 29, 20, 10, 27, 4, 80, 6, 41, 300, 333, 33, 43, 33, 115, 83000, 108512, -174026, -400, 1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0),
(56, 'Artisan', 4, 39, 45, 29, 20, 10, 27, 4, 80, 6, 41, 300, 333, 33, 43, 33, 115, 83000, 108512, -174026, -400, 1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0),
(57, 'Warsmith', 4, 39, 45, 29, 20, 10, 27, 4, 80, 6, 41, 300, 333, 33, 43, 33, 115, 83000, 108512, -174026, -400, 1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0),
(88, 'Duelist', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(89, 'DreadNought', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(90, 'Phoenix Knight', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(91, 'Hell Knight', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(92, 'Sagittarius', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(93, 'Adventurer', 0, 40, 43, 30, 21, 11, 25, 4, 80, 6, 41, 300, 333, 33, 44, 33, 115, 81900, -71338, 258271, -3104, 0, 1.10, 1.188000, 9.0, 23.0, 1.10, 1.188000, 8.0, 23.5),
(94, 'Archmage', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(95, 'Soultaker', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(96, 'Arcana Lord', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(97, 'Cardinal', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(98, 'Hierophant', 0, 22, 27, 21, 41, 20, 39, 3, 54, 6, 41, 300, 333, 28, 40, 28, 120, 62500, -90890, 248027, -3570, 0, 1.01, 0.872640, 7.5, 22.8, 1.01, 0.872640, 6.5, 22.5),
(99, 'Eva Templar', 1, 36, 36, 35, 23, 14, 26, 4, 80, 6, 41, 300, 333, 36, 46, 36, 125, 73000, 45978, 41196, -3440, 0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0),
(100, 'Sword Muse', 1, 36, 36, 35, 23, 14, 26, 4, 80, 6, 41, 300, 333, 36, 46, 36, 125, 73000, 45978, 41196, -3440, 0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0),
(101, 'Wind Rider', 1, 36, 36, 35, 23, 14, 26, 4, 80, 6, 41, 300, 333, 36, 46, 36, 125, 73000, 45978, 41196, -3440, 0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0),
(102, 'Moonlight Sentinel', 1, 36, 36, 35, 23, 14, 26, 4, 80, 6, 41, 300, 333, 36, 46, 36, 125, 73000, 45978, 41196, -3440, 0, 1.15, 1.242000, 7.5, 24.0, 1.15, 1.242000, 7.5, 23.0),
(103, 'Mystic Muse', 1, 21, 25, 24, 37, 23, 40, 3, 54, 6, 41, 300, 333, 30, 41, 30, 122, 62400, 46182, 41198, -3440, 0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0),
(104, 'Elemental Master', 1, 21, 25, 24, 37, 23, 40, 3, 54, 6, 41, 300, 333, 30, 41, 30, 122, 62400, 46182, 41198, -3440, 0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0),
(105, 'Eva Saint', 1, 21, 25, 24, 37, 23, 40, 3, 54, 6, 41, 300, 333, 30, 41, 30, 122, 62400, 46182, 41198, -3440, 0, 1.04, 0.898560, 7.5, 24.0, 1.04, 0.898560, 7.5, 23.0),
(106, 'Shillien Templar', 2, 41, 32, 34, 25, 12, 26, 4, 80, 6, 41, 300, 333, 35, 45, 35, 122, 69000, 28377, 10916, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5),
(107, 'Spectral Dancer', 2, 41, 32, 34, 25, 12, 26, 4, 80, 6, 41, 300, 333, 35, 45, 35, 122, 69000, 28377, 10916, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5),
(108, 'Ghost Hunter', 2, 41, 32, 34, 25, 12, 26, 4, 80, 6, 41, 300, 333, 35, 45, 35, 122, 69000, 28377, 10916, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5),
(109, 'Ghost Sentinel', 2, 41, 32, 34, 25, 12, 26, 4, 80, 6, 41, 300, 333, 35, 45, 35, 122, 69000, 28377, 10916, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.0, 23.5),
(110, 'Storm Screamer', 2, 23, 24, 23, 44, 19, 37, 3, 54, 6, 41, 300, 333, 29, 41, 29, 122, 61000, 28295, 11063, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5),
(111, 'Spectral Master', 2, 23, 24, 23, 44, 19, 37, 3, 54, 6, 41, 300, 333, 29, 41, 29, 122, 61000, 28295, 11063, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5),
(112, 'Shillen Saint', 2, 23, 24, 23, 44, 19, 37, 3, 54, 6, 41, 300, 333, 29, 41, 29, 122, 61000, 28295, 11063, -4224, 0, 1.14, 1.231200, 7.5, 24.0, 1.03, 0.889920, 7.0, 23.5),
(113, 'Titan', 3, 40, 47, 26, 18, 12, 27, 4, 80, 6, 41, 300, 333, 31, 42, 31, 117, 87000, -56693, -113610, -690, 0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0),
(114, 'Grand Khauatari', 3, 40, 47, 26, 18, 12, 27, 4, 80, 6, 41, 300, 333, 31, 42, 31, 117, 87000, -56693, -113610, -690, 0, 1.06, 1.144800, 11.0, 28.0, 1.06, 1.144800, 7.0, 27.0),
(115, 'Dominator', 3, 27, 31, 24, 31, 15, 42, 3, 54, 6, 41, 300, 333, 30, 41, 30, 121, 68000, -56682, -113730, -690, 0, 1.04, 0.898560, 7.0, 27.5, 1.04, 0.898560, 8.0, 25.5),
(116, 'Doomcryer', 3, 27, 31, 24, 31, 15, 42, 3, 54, 6, 41, 300, 333, 30, 41, 30, 121, 68000, -56682, -113730, -690, 0, 1.04, 0.898560, 7.0, 27.5, 1.04, 0.898560, 8.0, 25.5),
(117, 'Fortune Seeker', 4, 39, 45, 29, 20, 10, 27, 4, 80, 6, 41, 300, 333, 33, 43, 33, 115, 83000, 108512, -174026, -400, 1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0),
(118, 'Maestro', 4, 39, 45, 29, 20, 10, 27, 4, 80, 6, 41, 300, 333, 33, 43, 33, 115, 83000, 108512, -174026, -400, 1, 1.09, 1.487196, 9.0, 18.0, 1.09, 1.487196, 5.0, 19.0),
(123, 'Male Soldier', 5, 41, 31, 33, 29, 11, 25, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125464, 37776, 1176, 0, 1.14, 1.2312, 7.5, 24.0, 1.14, 1.2312, 7.5, 22.0),
(124, 'Female Soldier', 5, 39, 30, 35, 28, 11, 27, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125517, 38267, 1176, 0, 1.14, 1.2312, 7.5, 24.0, 1.14, 1.2312, 7.5, 22.0),
(125, 'Trooper', 5, 41, 31, 33, 29, 11, 25, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125533, 38114, 1142, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.5, 22.0),
(126, 'Warder', 5, 39, 30, 35, 28, 11, 27, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125533, 38114, 1142, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.5, 22.0),
(127, 'Berserker', 5, 41, 31, 33, 29, 11, 25, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125533, 38114, 1142, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.5, 22.0),
(128, 'Male Soulbreaker', 5, 41, 31, 33, 29, 11, 25, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125533, 38114, 1142, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.5, 22.0),
(129, 'Female Soulbreaker', 5, 39, 30, 35, 28, 11, 27, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125533, 38114, 1142, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.5, 22.0),
(130, 'Arbalester', 5, 39, 30, 35, 28, 11, 27, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125533, 38114, 1142, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.5, 22.0),
(131, 'Doombringer', 5, 41, 31, 33, 29, 11, 25, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125533, 38114, 1142, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.5, 22.0),
(132, 'Male Soulhound', 5, 41, 31, 33, 29, 11, 25, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125533, 38114, 1142, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.5, 22.0),
(133, 'Female Soulhound', 5, 39, 30, 35, 28, 11, 27, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125533, 38114, 1142, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.5, 22.0),
(134, 'Trickster', 5, 39, 30, 35, 28, 11, 27, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125533, 38114, 1142, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.5, 22.0),
(135, 'Inspector', 5, 39, 30, 35, 28, 11, 27, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125533, 38114, 1142, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.5, 22.0),
(136, 'Judicator', 5, 39, 30, 35, 28, 11, 27, 4, 72, 3, 47, 342, 333, 35, 45, 35, 122, 87000, -125533, 38114, 1142, 0, 1.14, 1.231200, 7.5, 24.0, 1.14, 1.231200, 7.5, 22.0);

-- TEMP FIX until Core-Side is improved to read `F_COL_R` & `F_COL_H` for Female Characters.
UPDATE char_templates SET M_COL_H=22.0 WHERE ClassId=124 OR ClassId=126 OR ClassId=129 OR ClassId=130 OR ClassId=133 OR ClassId=134;

--
-- L2J-Free Add-ons
--

-- Fix for dwarf female height
UPDATE `char_templates` SET `F_COL_H` = 18.0 WHERE `RaceId` = 4;
-- There's more than one point
ALTER TABLE `char_templates` DROP `x`;
ALTER TABLE `char_templates` DROP `y`;
ALTER TABLE `char_templates` DROP `z`;