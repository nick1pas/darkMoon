-- ---------------------------------------
--  Table structure for auto_announcements
-- ---------------------------------------
CREATE TABLE IF NOT EXISTS `auto_announcements` (
  `id` int(11) NOT NULL auto_increment,
  `announcement` varchar(255) collate latin1_general_ci NOT NULL,
  `delay` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;
-- -------------------------------------
--  Records for table auto_announcements
-- -------------------------------------

insert ignore into auto_announcements values 
(1, 'This Server is Powered by L2Emu Project Team.', 1400), 
(2, 'Visit us at : http://www.l2emu.net for additional support.', 1800);

-- L2Emu Project