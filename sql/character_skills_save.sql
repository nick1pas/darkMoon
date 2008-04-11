-- -----------------------------------------
-- Table structure for character_skills_save
-- -----------------------------------------
CREATE TABLE IF NOT EXISTS `character_skills_save` (
  `char_obj_id` int(11) NOT NULL default '0',
  `skill_id` int(11) NOT NULL default '0',
  `skill_level` int(11) NOT NULL default '0',
  `effect_count` int(11) NOT NULL default '0',
  `effect_cur_time` int(11) NOT NULL default '0',
  `reuse_delay` int(8) NOT NULL default '0',
  `restore_type` int(1) NOT NULL default '0',
  `class_index` int(1) NOT NULL default '0',
  `buff_index` INT(2) NOT NULL default '0',
  PRIMARY KEY  (`char_obj_id`,`skill_id`,`class_index`)
) DEFAULT CHARSET=utf8;

-- L2Emu Project