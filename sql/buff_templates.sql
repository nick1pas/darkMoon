-- ----------------------------------
-- Table structure for buff_templates
-- ----------------------------------
DROP TABLE IF EXISTS `buff_templates`; 
CREATE TABLE `buff_templates` (
  `id` int(11) unsigned NOT NULL,
  `name` varchar(35) NOT NULL default '',
  `skill_id` int(10) unsigned NOT NULL,
  `skill_name` varchar(35) default NULL,
  `skill_level` int(10) unsigned NOT NULL default '1',
  `skill_force` int(1) NOT NULL default '1',
  `skill_order` int(10) unsigned NOT NULL,
  `char_min_level` int(10) unsigned NOT NULL default '0',
  `char_max_level` int(10) unsigned NOT NULL default '0',
  `char_race` int(1) unsigned NOT NULL default '0',
  `char_class` int(1) NOT NULL default '0',
  `char_faction` int(10) unsigned NOT NULL default '0',
  `price_adena` int(10) unsigned NOT NULL default '0',
  `price_points` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`,`name`,`skill_order`)
) DEFAULT CHARSET=utf8;
-- --------------------------------
-- Records for table buff_templates
-- --------------------------------

--
-- id - template id, not zero value
-- name - template name, for easer access thru html links
-- Note: 'SupportMagic' is reserved name for Newbie Helper buff template
--
-- skill_id, skill_level - skill for buff
-- skill_force - force skill cast, eve if same skill effect present
-- skill_order - order of skill in template, not zero value
-- skill_name - name of skill
--
-- player condtitions for buff: 
-- 
-- char_min_level 
-- char_max_level - minimum and maximum level of character
--
-- char_race - race mask of character
-- 16 - human
-- 8  - elf
-- 4  - dark elf
-- 2  - orc
-- 1  - dwarf
-- example: if set 16+8+1, this mean 25, 
-- then buff can land only on humans,elfs and dwarves
-- set 0 or 31 for all races
--
-- char_class
-- 2 - magus
-- 1 - fighter
-- 0 or 3 - all classes
--
-- char_faction - faction id, 0 - for all or no faction
--
-- price_adena - amount of adena for buff
-- price_points - amount of faction poins for buff

insert into buff_templates values 

-- Reserved for Helper Buffer.
(1, 'SupportMagic', 4322, 'Wind Walk for Beginners', 1, 1, 1, 8, 39, 0, 0, 0, 0, 0), 
(1, 'SupportMagic', 4323, 'Shield for Beginners', 1, 1, 2, 11, 38, 0, 0, 0, 0, 0), 
(1, 'SupportMagic', 4338, 'Life Cubic for Beginners', 1, 1, 3, 16, 34, 0, 0, 0, 0, 0), 
(1, 'SupportMagic', 4324, 'Blessed Body for Beginners', 1, 1, 4, 12, 37, 0, 1, 0, 0, 0), 
(1, 'SupportMagic', 4325, 'Vampiric Rage for Beginners', 1, 1, 5, 13, 36, 0, 1, 0, 0, 0), 
(1, 'SupportMagic', 4326, 'Regeneration for Beginners', 1, 1, 6, 14, 35, 0, 1, 0, 0, 0), 
(1, 'SupportMagic', 4327, 'Haste for Beginners', 1, 1, 7, 15, 34, 0, 1, 0, 0, 0), 
(1, 'SupportMagic', 4328, 'Blessed Soul for Beginners', 1, 1, 8, 12, 37, 0, 2, 0, 0, 0), 
(1, 'SupportMagic', 4329, 'Acumen for Beginners', 1, 1, 9, 13, 36, 0, 2, 0, 0, 0), 
(1, 'SupportMagic', 4330, 'Concentration for Beginners', 1, 1, 10, 14, 35, 0, 2, 0, 0, 0), 
(1, 'SupportMagic', 4331, 'Empower for Beginners', 1, 1, 11, 15, 34, 0, 2, 0, 0, 0), 

-- Prophet Buffs
(2, 'Greater Empower', 1059, 'Greater Empower', 3, 1, 13, 1, 80, 0, 0, 0, 0, 0), 
(3, 'Acumen', 1085, 'Acumen', 3, 1, 12, 1, 80, 0, 0, 0, 0, 0), 
(4, 'Focus', 1077, 'Focus', 3, 1, 14, 1, 80, 0, 0, 0, 0, 0), 
(5, 'Greater Might', 1388, 'Greater Might', 3, 1, 15, 1, 80, 0, 0, 0, 0, 0), 
(6, 'Berserker Spirit', 1062, 'Berserker Spirit', 2, 1, 17, 1, 80, 0, 0, 0, 0, 0), 
(7, 'Greater Shield', 1389, 'Greater Shield', 3, 1, 16, 1, 80, 0, 0, 0, 0, 0), 
(8, 'Invigor', 1032, 'Invigor', 3, 1, 18, 1, 80, 0, 0, 0, 0, 0), 
(9, 'Mental Shield', 1035, 'Mental Shield', 4, 1, 19, 1, 80, 0, 0, 0, 0, 0), 
(10, 'Magic Barrier', 1036, 'Magic Barrier', 2, 1, 20, 1, 80, 0, 0, 0, 0, 0), 
(11, 'Regeneration', 1044, 'Regeneration', 3, 1, 21, 1, 80, 0, 0, 0, 0, 0), 
(12, 'Blessed Body', 1045, 'Blessed Body', 6, 1, 22, 1, 80, 0, 0, 0, 0, 0), 
(13, 'Blessed Soul', 1048, 'Blessed Soul', 6, 1, 23, 1, 80, 0, 0, 0, 0, 0), 
(350, 'Greater Concentration', 1078, 'Greater Concentration', 6, 1, 24, 1, 80, 0, 0, 0, 0, 0), 
(15, 'Haste', 1086, 'Haste', 2, 1, 25, 1, 80, 0, 0, 0, 0, 0), 
(16, 'Agility', 1087, 'Agility', 3, 1, 26, 1, 80, 0, 0, 0, 0, 0), 
(17, 'Wind Walk', 1204, 'Wind Walk', 2, 1, 27, 1, 80, 0, 0, 0, 0, 0), 
(18, 'Guidance', 1240, 'Guidance', 3, 1, 28, 1, 80, 0, 0, 0, 0, 0), 
(19, 'Death Whisper', 1242, 'Death Whisper', 3, 1, 29, 1, 80, 0, 0, 0, 0, 0), 
(20, 'Vampiric Rage', 1268, 'Vampiric Rage', 4, 1, 30, 1, 80, 0, 0, 0, 0, 0), 
(21, 'Bless Shield', 1243, 'Bless Shield', 6, 1, 31, 1, 80, 0, 0, 0, 0, 0), 

-- Dances
(22, 'Dance of Warrior', 271, 'Dance of Warrior', 1, 1, 32, 1, 80, 0, 0, 0, 0, 0), 
(23, 'Dance of Inspiration', 272, 'Dance of Inspiration', 1, 1, 33, 1, 80, 0, 0, 0, 0, 0), 
(24, 'Dance of Mystic', 273, 'Dance of Mystic', 1, 1, 34, 1, 80, 0, 0, 0, 0, 0), 
(25, 'Dance of Fire', 274, 'Dance of Fire', 1, 1, 35, 1, 80, 0, 0, 0, 0, 0), 
(26, 'Dance of Concentration', 276, 'Dance of Concentration', 1, 1, 36, 1, 80, 0, 0, 0, 0, 0), 
(27, 'Dance of Fury', 275, 'Dance of Fury', 1, 1, 37, 1, 80, 0, 0, 0, 0, 0), 
(28, 'Dance of Light', 277, 'Dance of Light', 1, 1, 38, 1, 80, 0, 0, 0, 0, 0), 
(29, 'Dance of Aqua Guard', 307, 'Dance of Aqua Guard', 1, 1, 39, 1, 80, 0, 0, 0, 0, 0), 
(30, 'Dance of Earth Guard', 309, 'Dance of Earth Guard', 1, 1, 40, 1, 80, 0, 0, 0, 0, 0), 
(31, 'Dance of Vampire', 310, 'Dance of Vampire', 1, 1, 41, 1, 80, 0, 0, 0, 0, 0), 
(32, 'Dance of Protection', 311, 'Dance of Protection', 1, 1, 42, 1, 80, 0, 0, 0, 0, 0), 
(33, 'Dance of Siren', 365, 'Dance of Siren', 1, 1, 43, 1, 80, 0, 0, 0, 0, 0), 
(300, 'Dance of Shadow', 366, 'Dance of Shadow', 1, 1, 44, 1, 80, 0, 0, 0, 0, 0),

-- Songs  
(35, 'Song of Earth', 264, 'Song of Earth', 1, 1, 45, 1, 80, 0, 0, 0, 0, 0), 
(36, 'Song of Life', 265, 'Song of Life', 1, 1, 46, 1, 80, 0, 0, 0, 0, 0), 
(37, 'Song of Water', 266, 'Song of Water', 1, 1, 47, 1, 80, 0, 0, 0, 0, 0), 
(38, 'Song of Warding', 267, 'Song of Warding', 1, 1, 48, 1, 80, 0, 0, 0, 0, 0), 
(39, 'Song of Wind', 268, 'Song of Wind', 1, 1, 49, 1, 80, 0, 0, 0, 0, 0), 
(40, 'Song of Hunter', 269, 'Song of Hunter', 1, 1, 50, 1, 80, 0, 0, 0, 0, 0), 
(41, 'Song of Invocation', 270, 'Song of Invocation', 1, 1, 51, 1, 80, 0, 0, 0, 0, 0), 
(42, 'Song of Vitality', 304, 'Song of Vitality', 1, 1, 52, 1, 80, 0, 0, 0, 0, 0), 
(43, 'Song of Vengeance', 305, 'Song of Vengeance', 1, 1, 53, 1, 80, 0, 0, 0, 0, 0), 
(44, 'Song of Flame Guard', 306, 'Song of Flame Guard', 1, 1, 54, 1, 80, 0, 0, 0, 0, 0), 
(45, 'Song of Storm Guard', 308, 'Song of Storm Guard', 1, 1, 55, 1, 80, 0, 0, 0, 0, 0), 
(46, 'Song of Renewal', 349, 'Song of Renewal', 1, 1, 56, 1, 80, 0, 0, 0, 0, 0), 
(47, 'Song of Meditation', 363, 'Song of Meditation', 1, 1, 57, 1, 80, 0, 0, 0, 0, 0), 
(48, 'Song of Champion', 364, 'Song of Champion', 1, 1, 58, 1, 80, 0, 0, 0, 0, 0), 

-- Reserved for cast all Dances in a Row.
(49, 'DanceSupport', 271, 'Dance of Warrior', 1, 1, 32, 1, 80, 0, 0, 0, 0, 0), 
(49, 'DanceSupport', 272, 'Dance of Inspiration', 1, 1, 33, 1, 80, 0, 0, 0, 0, 0), 
(49, 'DanceSupport', 273, 'Dance of Mystic', 1, 1, 34, 1, 80, 0, 0, 0, 0, 0), 
(49, 'DanceSupport', 274, 'Dance of Fire', 1, 1, 35, 1, 80, 0, 0, 0, 0, 0), 
(49, 'DanceSupport', 276, 'Dance of Concentration', 1, 1, 36, 1, 80, 0, 0, 0, 0, 0), 
(49, 'DanceSupport', 275, 'Dance of Fury', 1, 1, 37, 1, 80, 0, 0, 0, 0, 0), 
(49, 'DanceSupport', 277, 'Dance of Light', 1, 1, 38, 1, 80, 0, 0, 0, 0, 0), 
(49, 'DanceSupport', 307, 'Dance of Aqua Guard', 1, 1, 39, 1, 80, 0, 0, 0, 0, 0), 
(49, 'DanceSupport', 309, 'Dance of Earth Guard', 1, 1, 40, 1, 80, 0, 0, 0, 0, 0), 
(49, 'DanceSupport', 310, 'Dance of Vampire', 1, 1, 41, 1, 80, 0, 0, 0, 0, 0), 
(49, 'DanceSupport', 311, 'Dance of Protection', 1, 1, 42, 1, 80, 0, 0, 0, 0, 0), 
(49, 'DanceSupport', 365, 'Dance of Siren', 1, 1, 43, 1, 80, 0, 0, 0, 0, 0), 
(49, 'DanceSupport', 366, 'Dance of Shadow', 1, 1, 44, 1, 80, 0, 0, 0, 0, 0),

-- Reserved for cast all Songs in a row. 
(50, 'SongSupport', 264, 'Song of Earth', 1, 1, 45, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 265, 'Song of Life', 1, 1, 46, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 266, 'Song of Water', 1, 1, 47, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 267, 'Song of Warding', 1, 1, 48, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 268, 'Song of Wind', 1, 1, 49, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 269, 'Song of Hunter', 1, 1, 50, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 270, 'Song of Invocation', 1, 1, 51, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 304, 'Song of Vitality', 1, 1, 52, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 305, 'Song of Vengeance', 1, 1, 53, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 306, 'Song of Flame Guard', 1, 1, 54, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 308, 'Song of Storm Guard', 1, 1, 55, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 349, 'Song of Renewal', 1, 1, 56, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 363, 'Song of Meditation', 1, 1, 57, 1, 80, 0, 0, 0, 0, 0), 
(50, 'SongSupport', 364, 'Song of Champion', 1, 1, 58, 1, 80, 0, 0, 0, 0, 0), 

-- Reserved to cast all Prophet buffs in a row
(51, 'ProphetSupport', 1085, 'Acumen', 3, 1, 12, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1059, 'Greater Empower', 3, 1, 13, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1077, 'Focus', 3, 1, 14, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1388, 'Greater Might', 3, 1, 15, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1389, 'Greater Shield', 3, 1, 16, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1062, 'Berserker Spirit', 2, 1, 17, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1032, 'Invigor', 3, 1, 18, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1035, 'Mental Shield', 4, 1, 19, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1036, 'Magic Barrier', 2, 1, 20, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1044, 'Regeneration', 3, 1, 21, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1045, 'Blessed Body', 6, 1, 22, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1048, 'Blessed Soul', 6, 1, 23, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1078, 'Greater Concentration', 6, 1, 24, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1086, 'Haste', 2, 1, 25, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1087, 'Agility', 3, 1, 26, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1204, 'Wind Walk', 2, 1, 27, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1240, 'Guidance', 3, 1, 28, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1242, 'Death Whisper', 3, 1, 29, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1268, 'Vampiric Rage', 4, 1, 30, 1, 80, 0, 0, 0, 0, 0), 
(51, 'ProphetSupport', 1243, 'Bless Shield', 6, 1, 31, 1, 80, 0, 0, 0, 0, 0),

-- Shaman Buffs
(52, 'Flame Chant', 1002, 'Flame Chant', 3, 1, 32, 1, 80, 0, 0, 0, 0, 0),
(53, 'Pa\'agrian Gift', 1003, 'Pa\'agrian Gift', 3, 1, 33, 1, 80, 0, 0, 0, 0, 0),
(54, 'Blessings of Pa\'agrio', 1005, 'Blessings of Pa\'agrio', 3, 1, 34, 1, 80, 0, 0, 0, 0, 0),
(55, 'Chant of Fire', 1006, 'Chant of Fire', 3, 1, 35, 1, 80, 0, 0, 0, 0, 0),
(56, 'Chant of Battle', 1007, 'Chant of Battle', 2, 1, 36, 1, 80, 0, 0, 0, 0, 0),
(57, 'Chant of Shielding', 1009, 'Chant of Shielding', 3, 1, 37, 1, 80, 0, 0, 0, 0, 0),
(58, 'Soul Shield', 1010, 'Soul Shield', 3, 1, 38, 1, 80, 0, 0, 0, 0, 0),

-- Reserved to cast all shaman buffs in a row
(59, 'ShamanSupport', 1002, 'Flame Chant', 1, 1, 39, 1, 80, 0, 0, 0, 0, 0),
(59, 'ShamanSupport', 1003, 'Paagrian Gift', 3, 1, 40, 1, 80, 0, 0, 0, 0, 0),
(59, 'ShamanSupport', 1004, 'Blessings of Pa\'agrio', 3, 1, 41, 1, 80, 0, 0, 0, 0, 0),
(59, 'ShamanSupport', 1006, 'Chant of Fire', 3, 1, 42, 1, 80, 0, 0, 0, 0, 0),
(59, 'ShamanSupport', 1007, 'Chant of Battle', 2, 1, 43, 1, 80, 0, 0, 0, 0, 0),
(59, 'ShamanSupport', 1009, 'Chant of Shielding', 3, 1, 44, 1, 80, 0, 0, 0, 0, 0),
(59, 'ShamanSupport', 1010, 'Soul Shield', 3, 1, 45, 1, 80, 0, 0, 0, 0, 0),

-- Overlord Buffs
(60, 'The Wisdom of Pa\'agrio', 1004, 'The Wisdom of Pa\'agrio', 3, 1, 46, 1, 80, 0, 0, 0, 0, 0),
(61, 'The Glory of Pa\'agrio', 1008, 'The Glory of Pa\'agrio', 3, 1, 47, 1, 80, 0, 0, 0, 0, 0),
(62, 'The Vision of Pa\'agrio', 1249, 'The Vision of Pa\'agrio', 3, 1, 48, 1, 80, 0, 0, 0, 0, 0),
(63, 'Under the Protection of Pa\'agrio', 1250, 'Under the Protection of Pa\'agrio', 3, 1, 49, 1, 80, 0, 0, 0, 0, 0),
(64, 'The Tact of Pa\'agrio', 1260, 'The Tact of Pa\'agrio', 3, 1, 50, 1, 80, 0, 0, 0, 0, 0),
(65, 'The Rage of Pa\'agrio', 1261, 'The Rage of Pa\'agrio', 2, 1, 51, 1, 80, 0, 0, 0, 0, 0),
(66, 'The Honor of Pa\'agrio', 1305, 'The Honor of Pa\'agrio', 5, 1, 52, 1, 80, 0, 0, 0, 0, 0),
(67, 'The Eye of Pa\'agrio', 1364, 'The Eye of Pa\'agrio', 1, 1, 30, 1, 53, 0, 0, 0, 0, 0),
(68, 'The Soul of Pa\'agrio', 1365, 'The Soul of Pa\'agrio', 1, 1, 30, 1, 54, 0, 0, 0, 0, 0),
(301, 'The Heart of Pa\'agrio', 1256, 'The Heart of Pa\'agrio', 1, 1, 600, 1, 54, 0, 0, 0, 0, 0),

-- Reserved to cast all overlord buffs in a row
(69, 'OverlordSupport', 1004, 'The Wisdom of Pa\'agrio', 3, 1, 55, 1, 80, 0, 0, 0, 0, 0),
(69, 'OverlordSupport', 1008, 'The Glory of Pa\'agrio', 3, 1, 56, 1, 80, 0, 0, 0, 0, 0),
(69, 'OverlordSupport', 1249, 'The Vision of Pa\'agrio', 3, 1, 57, 1, 80, 0, 0, 0, 0, 0),
(69, 'OverlordSupport', 1250, 'Under the Protection of Pa\'agrio', 3, 1, 58, 1, 80, 0, 0, 0, 0, 0),
(69, 'OverlordSupport', 1260, 'The Tact of Pa\'agrio', 3, 1, 59, 1, 80, 0, 0, 0, 0, 0),
(69, 'OverlordSupport', 1261, 'The Rage of Pa\'agrio', 2, 1, 60, 1, 80, 0, 0, 0, 0, 0),
(69, 'OverlordSupport', 1305, 'The Honor of Pa\'agrio', 5, 1, 61, 1, 80, 0, 0, 0, 0, 0),
(69, 'OverlordSupport', 1364, 'The Eye of Pa\'agrio', 1, 1, 62, 1, 80, 0, 0, 0, 0, 0),
(69, 'OverlordSupport', 1365, 'The Soul of Pa\'agrio', 1, 1, 63, 1, 80, 0, 0, 0, 0, 0),
(69, 'OverlordSupport', 1256, 'The Heart of Pa\'agrio', 1, 1, 601, 1, 54, 0, 0, 0, 0, 0),

-- Warcryer Buffs 
(70, 'Chant of Fury', 1251, 'Chant of Fury', 2, 1, 64, 1, 80, 0, 0, 0, 0, 0),
(71, 'Chant of Evasion', 1252, 'Chant of Evasion',3, 1, 65, 1, 80, 0, 0, 0, 0, 0),
(72, 'Chant of Rage', 1253, 'Chant of Rage', 3, 1, 66, 1, 80, 0, 0, 0, 0, 0),
(73, 'Chant of Revenge', 1284, 'Chant of Revenge', 3, 1, 67, 1, 80, 0, 0, 0, 0, 0),
(74, 'Chant of Predator', 1308, 'Chant of Predator', 3, 1, 68, 1, 80, 0, 0, 0, 0, 0),
(75, 'Chant of Eagle',1309, 'Chant of Eagle', 3, 1, 69, 1, 80, 0, 0, 0, 0, 0),
(76, 'Chant of Vampire', 1310, 'Chant of Vampire', 4, 1, 70, 1, 80, 0, 0, 0, 0, 0),
(77, 'Chant of Spirit', 1362, 'Chant of Spirit', 1, 1, 30, 1, 71, 0, 0, 0, 0, 0),
(78, 'Chant of Victory', 1363, 'Chant of Victory', 1, 1, 72, 1, 80, 0, 0, 0, 0, 0),
(79, 'War Chant', 1390, 'War Chant', 3, 1, 73, 1, 80, 0, 0, 0, 0, 0),
(80, 'Earth Chant', 1391, 'Earth Chant', 3, 1, 74, 1, 80, 0, 0, 0, 0, 0),

-- Reserved to cast all warcryer buffs in a row 
(82, 'WarcryerSupport', 1251, 'Chant of Fury', 2, 1, 76, 1, 80, 0, 0, 0, 0, 0),
(82, 'WarcryerSupport', 1252, 'Chant of Evasion',3, 1, 77, 1, 80, 0, 0, 0, 0, 0),
(82, 'WarcryerSupport', 1253, 'Chant of Rage', 3, 1, 78, 1, 80, 0, 0, 0, 0, 0),
(82, 'WarcryerSupport', 1284, 'Chant of Revenge', 3, 1, 79, 1, 80, 0, 0, 0, 0, 0),
(82, 'WarcryerSupport', 1308, 'Chant of Predator', 3, 1, 80, 1, 80, 0, 0, 0, 0, 0),
(82, 'WarcryerSupport', 1309, 'Chant of Eagle', 3, 1, 81, 1, 80, 0, 0, 0, 0, 0),
(82, 'WarcryerSupport', 1310, 'Chant of Vampire', 4, 1, 82, 1, 80, 0, 0, 0, 0, 0),
(82, 'WarcryerSupport', 1362, 'Chant of Spirit', 1, 1, 83, 1, 80, 0, 0, 0, 0, 0),
(82, 'WarcryerSupport', 1363, 'Chant of Victory', 1, 1, 84, 1, 80, 0, 0, 0, 0, 0),
(82, 'WarcryerSupport', 1390, 'War Chant', 3, 1, 85, 1, 80, 0, 0, 0, 0, 0),
(82, 'WarcryerSupport', 1391, 'Earth Chant', 3, 1, 86, 1, 80, 0, 0, 0, 0, 0),

-- Cubics
(83, 'Summon Storm Cubic', 10, 'Summon Storm Cubic', 8, 1, 87, 1, 80, 0, 0, 0, 0, 0),
(84, 'Summon Phantom Cubic', 33, 'Summon Phantom Cubic', 8, 1, 88, 1, 80, 0, 0, 0, 0, 0),
(85, 'Summon Vampiric Cubic', 22, 'Summon Vampiric Cubic', 7, 1, 89, 1, 80, 0, 0, 0, 0, 0),
(86, 'Summon Life Cubic', 67, 'Summon Life Cubic', 7, 1, 90, 1, 80, 0, 0, 0, 0, 0),
(87, 'Summon Viper Cubic', 278, 'Summon Viper Cubic', 6, 1, 91, 1, 80, 0, 0, 0, 0, 0),
(88, 'Summon Binding Cubic', 1279, 'Summon Binding Cubic', 9, 1, 92, 1, 80, 0, 0, 0, 0, 0),
(89, 'Summon Aqua Cubic', 1280, 'Summon Aqua Cubic', 9, 1, 93, 1, 80, 0, 0, 0, 0, 0),
(90, 'Summon Spark Cubic', 1281, 'Summon Spark Cubic', 9, 1, 94, 1, 80, 0, 0, 0, 0, 0),

-- Hero Buffs
(92, 'Heroic Miracle', 395, 'Heroic Miracle', 1, 1, 30, 1, 95, 0, 0, 0, 0, 0),
(93, 'Heroic Berserker', 396, 'Heroic Berserker', 1, 1, 96, 1, 80, 0, 0, 0, 0, 0),
(94, 'Heroic Valor', 1374, 'Heroic Valor', 1, 1, 30, 1, 97, 0, 0, 0, 0, 0),

-- Reserved to cast all hero buffs in a row
(95, 'HeroSupport', 395, 'Heroic Miracle', 1, 1, 98, 1, 80, 0, 0, 0, 0, 0),
(95, 'HeroSupport', 395, 'Heroic Berserker', 1, 1, 99, 1, 80, 0, 0, 0, 0, 0),
(95, 'HeroSupport', 1374, 'Heroic Valor', 1, 1, 30, 1, 100, 0, 0, 0, 0, 0),

-- Noble Buffs
(96, 'Blessing of Noblesse', 1323, 'Blessing of Noblesse', 1, 1, 101, 1, 80, 0, 0, 0, 0, 0),
(97, 'Fortune of Noblesse', 1325, 'Fortune of Noblesse', 1, 1, 102, 1, 80, 0, 0, 0, 0, 0),

-- Reserved to cast all noble buffs in a row
(98, 'NobleSupport', 1323, 'Blessing of Noblesse', 1, 1, 103, 1, 80, 0, 0, 0, 0, 0),   
(98, 'NobleSupport', 1325, 'Fortune of Noblesse', 1, 1, 104, 1, 80, 0, 0, 0, 0, 0),

-- Reserved to cast all summon buffs in a row
(99, 'SummonSupport', 4699, 'Blessing of Queen', 13, 1, 105, 1, 80, 0, 0, 0, 0, 0),
(99, 'SummonSupport', 4700, 'Gift of Queen', 13, 1, 106, 1, 80, 0, 0, 0, 0, 0),
(99, 'SummonSupport', 4701, 'Blessing of Seraphim', 13, 1, 107, 1, 80, 0, 0, 0, 0, 0),
(99, 'SummonSupport', 4702, 'Gift of Serapfim', 13, 1, 108, 1, 80, 0, 0, 0, 0, 0),
(99, 'SummonSupport', 4703, 'Cure of Queen', 13, 1, 109, 1, 80, 0, 0, 0, 0, 0),
(99, 'SummonSupport', 4704, 'Cure of Seraphim', 13, 1, 110, 1, 80, 0, 0, 0, 0, 0),

-- Summons Buffs
(100, 'Blessing of Queen', 4699, 'Blessing of Queen', 13, 1, 111, 1, 80, 0, 0, 0, 0, 0),
(101, 'Gift of Queen', 4700, 'Gift of Queen', 13, 1, 112, 1, 80, 0, 0, 0, 0, 0),
(102, 'Cure of Queen', 4701, 'Cure of Queen', 13, 1, 113, 1, 80, 0, 0, 0, 0, 0),
(103, 'Blessing of Seraphim', 4702, 'Blessing of Seraphim', 13, 1, 114, 1, 80, 0, 0, 0, 0, 0),
(104, 'Gift of Serapfim', 4703, 'Gift of Serapfim', 13, 1, 115, 1, 80, 0, 0, 0, 0, 0),
(105, 'Cure of Seraphim', 4704, 'Cure of Seraphim', 13, 1, 116, 1, 80, 0, 0, 0, 0, 0),

-- Other Buffs
(106, 'Prophecy of Water', 1355, 'Prophecy of Water', 1, 1, 117, 1, 80, 0, 0, 0, 0, 0),
(107, 'Prophecy of Fire', 1356, 'Prophecy of Fire', 1, 1, 118, 1, 80, 0, 0, 0, 0, 0),
(108, 'Prophecy of Wind', 1357, 'Prophecy of Wind', 1, 1, 119, 1, 80, 0, 0, 0, 0, 0),
(109, 'Wild Magic', 1303, 'Wild Magic', 2, 1, 120, 1, 80, 0, 0, 0, 0, 0),
(110, 'Migth', 1068, 'Migth', 3, 1, 121, 1, 80, 0, 0, 0, 0, 0),
(111, 'Shield', 1040, 'Shield', 3, 1, 122, 1, 80, 0, 0, 0, 0, 0),
(112, 'Unholy Resistence', 1393, 'Unholy Resistence', 3, 1, 123, 1, 80, 0, 0, 0, 0, 0),
(113, 'Holy Resistence', 1392, 'Holy Resistence', 1, 3, 124, 1, 80, 0, 0, 0, 0, 0),
(114, 'Elemental Protection', 1352, 'Elemental Protection', 1, 1, 125, 1, 80, 0, 0, 0, 0, 0),
(115, 'Divine Protection', 1353, 'Divine Protection', 1, 1, 126, 1, 80, 0, 0, 0, 0, 0),
(116, 'Resist Shock', 1259, 'Resist Shock', 4, 1, 127, 1, 80, 0, 0, 0, 0, 0),
(117, 'Arcane Protection', 1354, 'Arcane Protection', 1, 1, 128, 1, 80, 0, 0, 0, 0, 0),
(118, 'Body of Avatar', 1311, 'Body of Avatar', 6, 1, 129, 1, 80, 0, 0, 0, 0, 0);

-- L2Emu Project