DROP TABLE IF EXISTS `certification_skill_trees`;
CREATE TABLE `certification_skill_trees` (
  `skill_id` int(6) NOT NULL,
  `item_id` int(6) NOT NULL,
  `level` int(3) NOT NULL,
  `name` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`skill_id`,`level`)
) DEFAULT CHARSET=utf8;

INSERT INTO `certification_skill_trees` (`skill_id`, `item_id`, `level`, `name`) VALUES 
(631, 10280, 1, 'Emergent Ability - Attack'),
(631, 10280, 2, 'Emergent Ability - Attack'),
(631, 10280, 3, 'Emergent Ability - Attack'),
(631, 10280, 4, 'Emergent Ability - Attack'),
(631, 10280, 5, 'Emergent Ability - Attack'),
(631, 10280, 6, 'Emergent Ability - Attack'),
(632, 10280, 1, 'Emergent Ability - Defense'),
(632, 10280, 2, 'Emergent Ability - Defense'),
(632, 10280, 3, 'Emergent Ability - Defense'),
(632, 10280, 4, 'Emergent Ability - Defense'),
(632, 10280, 5, 'Emergent Ability - Defense'),
(632, 10280, 6, 'Emergent Ability - Defense'),
(633, 10280, 1, 'Emergent Ability - Empower'),
(633, 10280, 2, 'Emergent Ability - Empower'),
(633, 10280, 3, 'Emergent Ability - Empower'),
(633, 10280, 4, 'Emergent Ability - Empower'),
(633, 10280, 5, 'Emergent Ability - Empower'),
(633, 10280, 6, 'Emergent Ability - Empower'),
(634, 10280, 1, 'Emergent Ability - Magic Defense'),
(634, 10280, 2, 'Emergent Ability - Magic Defense'),
(634, 10280, 3, 'Emergent Ability - Magic Defense'),
(634, 10280, 4, 'Emergent Ability - Magic Defense'),
(634, 10280, 5, 'Emergent Ability - Magic Defense'),
(634, 10280, 6, 'Emergent Ability - Magic Defense'),
(637, 10612, 1, 'Master Ability - Attack'),
(638, 10612, 1, 'Master Ability - Empower'),
(639, 10612, 1, 'Master Ability - Casting'),
(640, 10612, 1, 'Master Ability - Focus'),
(641, 10282, 1, 'Knight Ability - Boost HP'),
(642, 10287, 1, 'Enchanter Ability - Boost Mana'),
(643, 10286, 1, 'Summoner Ability - Boost HP/MP'),
(644, 10283, 1, 'Rogue Ability - Evasion'),
(645, 10283, 1, 'Rogue Ability - Long Shot'),
(646, 10284, 1, 'Wizard Ability - Mana Gain'),
(647, 10287, 1, 'Enchanter Ability - Mana Recovery'),
(648, 10285, 1, 'Healer Ability - Prayer'),
(650, 10281, 1, 'Warrior Ability - Resist Trait'),
(651, 10281, 1, 'Warrior Ability - Haste'),
(652, 10282, 1, 'Knight Ability - Defense'),
(653, 10283, 1, 'Rogue Ability - Critical Chance'),
(654, 10284, 1, 'Wizard Ability - Mana Steal'),
(655, 10287, 1, 'Enchanter Ability - Barrier'),
(799, 10612, 1, 'Master Ability - Defense'),
(799, 10612, 2, 'Master Ability - Defense'),
(799, 10612, 3, 'Master Ability - Defense'),
(800, 10612, 1, 'Master Ability - Magic Defense'),
(800, 10612, 2, 'Master Ability - Magic Defense'),
(800, 10612, 3, 'Master Ability - Magic Defense'),
(801, 10281, 1, 'Warrior Ability - Boost CP'),
(802, 10284, 1, 'Wizard Ability - Anti-magic'),
(803, 10285, 1, 'Healer Ability - Divine Protection'),
(804, 10282, 1, 'Knight Ability - Resist Critical'),
(1489, 10286, 1, 'Summoner Ability - Resist Attribute'),
(1490, 10285, 1, 'Healer Ability - Heal'),
(1491, 10286, 1, 'Summoner Ability - Spirit');
