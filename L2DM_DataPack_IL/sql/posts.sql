-- Dark Moon Dev Team
CREATE TABLE `posts` (
  `post_id` int(8) NOT NULL auto_increment,
  `post_owner_name` varchar(255) NOT NULL default '',
  `post_ownerid` int(8) NOT NULL default '0',
  `post_date` decimal(20,0) NOT NULL default '0',
  `post_topic_id` int(8) NOT NULL default '0',
  `post_txt` text NOT NULL,
  PRIMARY KEY  (`post_id`),
  KEY `post_topic_id` (`post_topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
