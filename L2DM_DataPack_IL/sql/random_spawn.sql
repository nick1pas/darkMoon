-- Dark Moon Dev Team
DROP TABLE IF EXISTS `random_spawn`;
CREATE TABLE `random_spawn` (
  `groupId` int(11) NOT NULL default '0',
  `npcId` int(11) NOT NULL default '0',
  `count` int(11) NOT NULL default '0',
  `initialDelay` bigint(20) NOT NULL default '-1',
  `respawnDelay` bigint(20) NOT NULL default '-1',
  `despawnDelay` bigint(20) NOT NULL default '-1',
  `broadcastSpawn` varchar(5) NOT NULL default 'false',
  `randomSpawn` varchar(5) NOT NULL default 'true',
  PRIMARY KEY  (`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `random_spawn` VALUES 
('1', '30556', '1', '-1', '1800000', '1800000', 'false', 'true'),
('9', '31111', '1', '-1', '60', '0', 'false', 'false'),
('10', '31112', '1', '-1', '60', '0', 'false', 'false'),
('11', '31113', '1', '-1', '-1', '-1', 'true', 'true'),
('12', '31126', '1', '-1', '-1', '-1', 'true', 'true'),
('13', '31094', '1', '-1', '60', '0', 'false', 'false'),
('14', '31094', '1', '-1', '60', '0', 'false', 'false'),
('15', '31094', '1', '-1', '60', '0', 'false', 'false'),
('16', '31094', '1', '-1', '60', '0', 'false', 'false'),
('17', '31094', '1', '-1', '60', '0', 'false', 'false'),
('18', '31094', '1', '-1', '60', '0', 'false', 'false'),
('19', '31094', '1', '-1', '60', '0', 'false', 'false'),
('20', '31094', '1', '-1', '60', '0', 'false', 'false'),
('21', '31094', '1', '-1', '60', '0', 'false', 'false'),
('22', '31094', '1', '-1', '60', '0', 'false', 'false'),
('23', '31094', '1', '-1', '60', '0', 'false', 'false'),
('24', '31094', '1', '-1', '60', '0', 'false', 'false'),
('25', '31094', '1', '-1', '60', '0', 'false', 'false'),
('26', '31094', '1', '-1', '60', '0', 'false', 'false'),
('27', '31094', '1', '-1', '60', '0', 'false', 'false'),
('28', '31094', '1', '-1', '60', '0', 'false', 'false'),
('29', '31094', '1', '-1', '60', '0', 'false', 'false'),
('30', '31094', '1', '-1', '60', '0', 'false', 'false'),
('31', '31094', '1', '-1', '60', '0', 'false', 'false'),
('32', '31094', '1', '-1', '60', '0', 'false', 'false'),
('33', '31094', '1', '-1', '60', '0', 'false', 'false'),
('34', '31094', '1', '-1', '60', '0', 'false', 'false'),
('35', '31094', '1', '-1', '60', '0', 'false', 'false'),
('36', '31094', '1', '-1', '60', '0', 'false', 'false'),
('37', '31094', '1', '-1', '60', '0', 'false', 'false'),
('38', '31094', '1', '-1', '60', '0', 'false', 'false'),
('39', '31094', '1', '-1', '60', '0', 'false', 'false'),
('40', '31094', '1', '-1', '60', '0', 'false', 'false'),
('41', '31093', '1', '-1', '60', '0', 'false', 'false'),
('42', '31093', '1', '-1', '60', '0', 'false', 'false'),
('43', '31093', '1', '-1', '60', '0', 'false', 'false'),
('44', '31093', '1', '-1', '60', '0', 'false', 'false'),
('45', '31093', '1', '-1', '60', '0', 'false', 'false'),
('46', '31093', '1', '-1', '60', '0', 'false', 'false'),
('47', '31093', '1', '-1', '60', '0', 'false', 'false'),
('48', '31093', '1', '-1', '60', '0', 'false', 'false'),
('49', '31093', '1', '-1', '60', '0', 'false', 'false'),
('50', '31093', '1', '-1', '60', '0', 'false', 'false'),
('51', '31093', '1', '-1', '60', '0', 'false', 'false'),
('52', '31093', '1', '-1', '60', '0', 'false', 'false'),
('53', '31093', '1', '-1', '60', '0', 'false', 'false'),
('54', '31093', '1', '-1', '60', '0', 'false', 'false'),
('55', '31093', '1', '-1', '60', '0', 'false', 'false'),
('56', '31093', '1', '-1', '60', '0', 'false', 'false'),
('57', '31093', '1', '-1', '60', '0', 'false', 'false'),
('58', '31093', '1', '-1', '60', '0', 'false', 'false'),
('59', '31093', '1', '-1', '60', '0', 'false', 'false'),
('60', '31093', '1', '-1', '60', '0', 'false', 'false'),
('61', '31093', '1', '-1', '60', '0', 'false', 'false'),
('62', '31093', '1', '-1', '60', '0', 'false', 'false'),
('63', '31093', '1', '-1', '60', '0', 'false', 'false'),
('64', '31093', '1', '-1', '60', '0', 'false', 'false'),
('65', '31093', '1', '-1', '60', '0', 'false', 'false'),
('66', '31093', '1', '-1', '60', '0', 'false', 'false'),
('67', '31093', '1', '-1', '60', '0', 'false', 'false'),
('68', '31093', '1', '-1', '60', '0', 'false', 'false'),
('69', '31093', '1', '-1', '60', '0', 'false', 'false'),
('70', '31093', '1', '-1', '60', '0', 'false', 'false'),
('71', '31093', '1', '-1', '60', '0', 'false', 'false'),
('72', '31093', '1', '-1', '60', '0', 'false', 'false'),
('73', '31093', '1', '-1', '60', '0', 'false', 'false'),
('74', '31093', '1', '-1', '60', '0', 'false', 'false'),
('75', '31093', '1', '-1', '60', '0', 'false', 'false'),
('76', '31093', '1', '-1', '60', '0', 'false', 'false'),
('77', '31093', '1', '-1', '60', '0', 'false', 'false'),
('78', '31093', '1', '-1', '60', '0', 'false', 'false'),
('79', '31093', '1', '-1', '60', '0', 'false', 'false'),
('80', '31093', '1', '-1', '60', '0', 'false', 'false'),
('81', '31093', '1', '-1', '60', '0', 'false', 'false'),
('82', '31170', '1', '-1', '60', '0', 'false', 'false'),
('83', '31171', '1', '-1', '60', '0', 'false', 'false'),
('84', '31170', '1', '-1', '60', '0', 'false', 'false'),
('85', '31171', '1', '-1', '60', '0', 'false', 'false'),
('86', '31170', '1', '-1', '60', '0', 'false', 'false'),
('87', '31171', '1', '-1', '60', '0', 'false', 'false'),
('88', '31170', '1', '-1', '60', '0', 'false', 'false'),
('89', '31171', '1', '-1', '60', '0', 'false', 'false'),
('90', '31170', '1', '-1', '60', '0', 'false', 'false'),
('91', '31171', '1', '-1', '60', '0', 'false', 'false'),
('92', '31170', '1', '-1', '60', '0', 'false', 'false'),
('93', '31171', '1', '-1', '60', '0', 'false', 'false'),
('94', '31170', '1', '-1', '60', '0', 'false', 'false'),
('95', '31171', '1', '-1', '60', '0', 'false', 'false'),
('96', '31170', '1', '-1', '60', '0', 'false', 'false'),
('97', '31171', '1', '-1', '60', '0', 'false', 'false'),
('98', '31170', '1', '-1', '60', '0', 'false', 'false'),
('99', '31171', '1', '-1', '60', '0', 'false', 'false'),
('100', '31170', '1', '-1', '60', '0', 'false', 'false'),
('101', '31171', '1', '-1', '60', '0', 'false', 'false'),
('102', '31170', '1', '-1', '60', '0', 'false', 'false'),
('103', '31171', '1', '-1', '60', '0', 'false', 'false'),
('104', '31170', '1', '-1', '60', '0', 'false', 'false'),
('105', '31171', '1', '-1', '60', '0', 'false', 'false'),
('106', '31170', '1', '-1', '60', '0', 'false', 'false'),
('107', '31171', '1', '-1', '60', '0', 'false', 'false'),
('108', '31170', '1', '-1', '60', '0', 'false', 'false'),
('109', '31171', '1', '-1', '60', '0', 'false', 'false'),
('110', '25283', '1', '-1', '86400', '0', 'false', 'false'),
('111', '25286', '1', '-1', '86400', '0', 'false', 'false'),
('112', '27316', '1', '1800000', '14400000', '1800000', 'false', 'false'),
('113', '31094', '1', '-1', '60', '0', 'false', 'false'),
('114', '31093', '1', '-1', '60', '0', 'false', 'false'),
('115', '31094', '1', '-1', '60', '0', 'false', 'false'),
('116', '31093', '1', '-1', '60', '0', 'false', 'false'),
('117', '31094', '1', '-1', '60', '0', 'false', 'false'),
('118', '31093', '1', '-1', '60', '0', 'false', 'false'),
('119', '31094', '1', '-1', '60', '0', 'false', 'false'),
('120', '31093', '1', '-1', '60', '0', 'false', 'false'),
('121', '31093', '1', '-1', '60', '0', 'false', 'false'),
('122', '31094', '1', '-1', '60', '0', 'false', 'false'),
('123', '31093', '1', '-1', '60', '0', 'false', 'false'),
('124', '31093', '1', '-1', '60', '0', 'false', 'false'),
('125', '31094', '1', '-1', '60', '0', 'false', 'false'),
('126', '31094', '1', '-1', '60', '0', 'false', 'false'),
('127', '31093', '1', '-1', '60', '0', 'false', 'false'),
('128', '31094', '1', '-1', '60', '0', 'false', 'false'),
('129', '31093', '1', '-1', '60', '0', 'false', 'false'),
('130', '31094', '1', '-1', '60', '0', 'false', 'false'),
('131', '31093', '1', '-1', '60', '0', 'false', 'false'),
('132', '31094', '1', '-1', '60', '0', 'false', 'false'),
('133', '32014', '1', '-1', '1800000', '1800000', 'false', 'true'),
('134', '32013', '1', '-1', '1800000', '1800000', 'false', 'true'),
('135', '32049', '1', '-1', '1200000', '1200000', 'false', 'true'),
('136', '32012', '1', '-1', '1800000', '1800000', 'false', 'true');
('137', '57777', '1', '-1', '1800000', '1800000', 'true', 'true');
