-- Dark Moon Dev Team
CREATE TABLE `auto_announcements` (
  `id` int(11) NOT NULL auto_increment,
  `announcement` varchar(255) collate latin1_general_ci NOT NULL default '',
  `delay` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;

INSERT INTO `auto_announcements` VALUES ('1', 'Server Software developed by ShT Dev Team.', '1400');
