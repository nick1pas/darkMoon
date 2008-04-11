/*
MySQL Data Transfer
Source Host: localhost
Source Database: l2jdb
Target Host: localhost
Target Database: l2jdb
Date: 28.01.2008 20:05:41
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for gameservers
-- ----------------------------
CREATE TABLE `gameservers` (
  `server_id` int(11) NOT NULL default '0',
  `hexid` varchar(50) NOT NULL default '',
  `host` varchar(50) NOT NULL default '',
  PRIMARY KEY  (`server_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------

INSERT INTO `gameservers` VALUES ('2', '7243727da2fc2925dc064001a6173bcf', '10.48.5.215');
