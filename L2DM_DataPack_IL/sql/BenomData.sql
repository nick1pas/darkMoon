-- Dark Moon Dev Team
DROP TABLE IF EXISTS `benomdata`;
CREATE TABLE `benomdata` (
  `benomstate` varchar(15) NOT NULL DEFAULT 'NOTSPAWNED'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `benomdata` VALUES ('KILLED');
