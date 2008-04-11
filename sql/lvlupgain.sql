-- -----------------------------
-- Table structure for lvlupgain
-- -----------------------------
DROP TABLE IF EXISTS `lvlupgain`;
CREATE TABLE `lvlupgain` (
  `classid` int(3) NOT NULL default '0',
  `defaulthpbase` decimal(5,1) NOT NULL default '0.0',
  `defaulthpadd` decimal(4,2) NOT NULL default '0.00',
  `defaulthpmod` decimal(4,2) NOT NULL default '0.00',
  `defaultcpbase` decimal(5,1) NOT NULL default '0.0',
  `defaultcpadd` decimal(4,2) NOT NULL default '0.00',
  `defaultcpmod` decimal(4,2) NOT NULL default '0.00',
  `defaultmpbase` decimal(5,1) NOT NULL default '0.0',
  `defaultmpadd` decimal(4,2) NOT NULL default '0.00',
  `defaultmpmod` decimal(4,2) NOT NULL default '0.00',
  `class_lvl` int(3) NOT NULL default '0',
  PRIMARY KEY  (`classid`)
) DEFAULT CHARSET=utf8;
-- ---------------------------
-- Records for table lvlupgain
-- ---------------------------

INSERT INTO `lvlupgain` VALUES
('0', '80.0', '11.83', '0.37', '32.0', '4.73', '0.22', '30.0', '5.46', '0.14', '1'),
('1', '327.0', '33.00', '0.37', '261.6', '26.40', '0.22', '144.0', '9.90', '0.14', '20'),
('2', '1044.0', '49.40', '0.37', '939.6', '44.46', '0.22', '359.1', '19.50', '0.14', '40'),
('3', '1044.0', '54.60', '0.37', '835.2', '43.68', '0.22', '359.1', '19.50', '0.14', '40'),
('4', '327.0', '29.70', '0.37', '196.2', '17.82', '0.22', '144.0', '9.90', '0.14', '20'),
('5', '972.3', '46.80', '0.37', '583.3', '28.08', '0.22', '359.1', '19.50', '0.14', '40'),
('6', '972.3', '46.80', '0.37', '583.3', '28.08', '0.22', '359.1', '19.50', '0.14', '40'),
('7', '327.0', '27.50', '0.37', '130.8', '11.00', '0.22', '144.0', '9.90', '0.14', '20'),
('8', '924.5', '41.60', '0.37', '369.8', '16.64', '0.22', '359.1', '19.50', '0.14', '40'),
('9', '924.5', '44.20', '0.37', '647.1', '30.94', '0.22', '359.1', '19.50', '0.14', '40'),
('10', '101.0', '15.57', '0.37', '50.5', '7.84', '0.22', '40.0', '7.38', '0.14', '1'),
('11', '424.0', '27.60', '0.37', '212.0', '13.85', '0.22', '192.0', '13.30', '0.14', '20'),
('12', '1021.5', '45.60', '0.37', '510.7', '22.85', '0.22', '478.8', '26.10', '0.14', '40'),
('13', '1021.5', '45.60', '0.37', '510.7', '22.85', '0.22', '478.8', '26.10', '0.14', '40'),
('14', '1021.5', '49.50', '0.37', '612.9', '29.74', '0.22', '478.8', '26.10', '0.14', '40'),
('15', '424.0', '34.20', '0.37', '212.0', '17.15', '0.22', '192.0', '13.30', '0.14', '20'),
('16', '1164.9', '49.50', '0.37', '815.4', '34.68', '0.22', '478.8', '26.10', '0.14', '40'),
('17', '1164.9', '53.40', '0.37', '582.4', '26.75', '0.22', '478.8', '26.10', '0.14', '40'),
('18', '89.0', '12.74', '0.37', '35.6', '5.00', '0.22', '30.0', '5.46', '0.14', '1'),
('19', '355.0', '33.00', '0.37', '177.5', '16.50', '0.22', '144.0', '9.90', '0.14', '20'),
('20', '1072.0', '52.00', '0.37', '643.2', '31.20', '0.22', '359.1', '19.50', '0.14', '40'),
('21', '1072.0', '54.60', '0.37', '536.0', '27.30', '0.22', '359.1', '19.50', '0.14', '40'),
('22', '355.0', '30.80', '0.37', '142.0', '12.32', '0.22', '144.0', '9.90', '0.14', '20'),
('23', '1024.2', '46.80', '0.37', '409.6', '18.72', '0.22', '359.1', '19.50', '0.14', '40'),
('24', '1024.2', '49.40', '0.37', '512.1', '24.70', '0.22', '359.1', '19.50', '0.14', '40'),
('25', '104.0', '15.57', '0.37', '52.0', '7.84', '0.22', '40.0', '7.38', '0.14', '1'),
('26', '427.0', '28.70', '0.37', '213.5', '14.40', '0.22', '192.0', '13.30', '0.14', '20'),
('27', '1048.4', '48.20', '0.37', '524.2', '24.15', '0.22', '478.8', '26.10', '0.14', '40'),
('28', '1048.4', '50.80', '0.37', '629.0', '30.52', '0.22', '478.8', '26.10', '0.14', '40'),
('29', '427.0', '35.30', '0.37', '213.5', '17.70', '0.22', '192.0', '13.30', '0.14', '20'),
('30', '1191.8', '54.70', '0.37', '595.9', '27.40', '0.22', '478.8', '26.10', '0.14', '40'),
('31', '94.0', '13.65', '0.37', '37.6', '5.46', '0.22', '30.0', '5.46', '0.14', '1'),
('32', '379.0', '35.20', '0.37', '189.5', '17.60', '0.22', '144.0', '9.90', '0.14', '20'),
('33', '1143.8', '54.60', '0.37', '686.2', '32.76', '0.22', '359.1', '19.50', '0.14', '40'),
('34', '1143.8', '58.50', '0.37', '571.9', '29.25', '0.22', '359.1', '19.50', '0.14', '40'),
('35', '379.0', '33.00', '0.37', '151.6', '13.20', '0.22', '144.0', '9.90', '0.14', '20'),
('36', '1096.0', '49.40', '0.37', '438.4', '19.76', '0.22', '359.1', '19.50', '0.14', '40'),
('37', '1096.0', '52.00', '0.37', '548.0', '26.00', '0.22', '359.1', '19.50', '0.14', '40'),
('38', '106.0', '15.57', '0.37', '53.0', '7.84', '0.22', '40.0', '7.38', '0.14', '1'),
('39', '429.0', '29.80', '0.37', '214.5', '14.95', '0.22', '192.0', '13.30', '0.14', '20'),
('40', '1074.3', '48.20', '0.37', '537.1', '24.15', '0.22', '478.8', '26.10', '0.14', '40'),
('41', '1074.3', '52.10', '0.37', '644.5', '31.30', '0.22', '478.8', '26.10', '0.14', '40'),
('42', '429.0', '36.40', '0.37', '214.5', '18.25', '0.22', '192.0', '13.30', '0.14', '20'),
('43', '1217.7', '54.70', '0.37', '608.8', '27.40', '0.22', '478.8', '26.10', '0.14', '40'),
('44', '80.0', '12.64', '0.37', '40.0', '6.27', '0.22', '30.0', '5.36', '0.14', '1'),
('45', '346.0', '35.10', '0.37', '242.2', '24.54', '0.22', '144.0', '9.80', '0.14', '20'),
('46', '1110.8', '57.10', '0.37', '777.5', '39.94', '0.22', '359.1', '19.40', '0.14', '40'),
('47', '346.0', '32.90', '0.37', '173.0', '16.40', '0.22', '144.0', '9.80', '0.14', '20'),
('48', '1063.0', '54.50', '0.37', '531.5', '27.20', '0.22', '359.1', '19.40', '0.14', '40'),
('49', '95.0', '15.47', '0.37', '47.5', '7.74', '0.22', '40.0', '7.28', '0.14', '1'),
('50', '418.0', '35.20', '0.37', '209.0', '17.60', '0.22', '192.0', '13.20', '0.14', '20'),
('51', '1182.8', '53.30', '0.37', '946.2', '42.64', '0.22', '478.8', '26.00', '0.14', '40'),
('52', '1182.8', '53.30', '0.37', '591.4', '26.65', '0.22', '478.8', '26.00', '0.14', '40'),
('53', '80.0', '12.64', '0.37', '56.0', '8.82', '0.22', '30.0', '5.36', '0.14', '1'),
('54', '346.0', '35.10', '0.37', '242.2', '24.54', '0.22', '144.0', '9.80', '0.14', '20'),
('55', '1110.8', '57.10', '0.37', '777.5', '39.94', '0.22', '359.1', '19.40', '0.14', '40'),
('56', '346.0', '32.90', '0.37', '276.8', '26.30', '0.22', '144.0', '9.80', '0.14', '20'),
('57', '1063.0', '54.50', '0.37', '850.4', '43.58', '0.22', '359.1', '19.40', '0.14', '40'),
('88', '3061.8', '63.08', '0.37', '2755.6', '56.77', '0.22', '1155.6', '24.90', '0.14', '76'),
('89', '3274.2', '69.72', '0.37', '2619.3', '55.78', '0.22', '1155.6', '24.90', '0.14', '76'),
('90', '2883.9', '59.76', '0.37', '1730.3', '35.86', '0.22', '1155.6', '24.90', '0.14', '76'),
('91', '2883.9', '59.76', '0.37', '1730.3', '35.86', '0.22', '1155.6', '24.90', '0.14', '76'),
('92', '2729.9', '56.44', '0.37', '1910.9', '39.51', '0.22', '1155.6', '24.90', '0.14', '76'),
('93', '2623.7', '53.12', '0.37', '1049.4', '21.25', '0.22', '1155.6', '24.90', '0.14', '76'),
('94', '2880.0', '58.10', '0.37', '1440.0', '29.05', '0.22', '1540.8', '33.20', '0.14', '76'),
('95', '2880.0', '58.10', '0.37', '1440.0', '29.05', '0.22', '1540.8', '33.20', '0.14', '76'),
('96', '3039.3', '63.08', '0.37', '1823.5', '37.85', '0.22', '1540.8', '33.20', '0.14', '76'),
('97', '3182.7', '63.08', '0.37', '2227.8', '44.16', '0.22', '1540.8', '33.20', '0.14', '76'),
('98', '3342.0', '68.06', '0.37', '1671.0', '34.03', '0.22', '1540.8', '33.20', '0.14', '76'),
('99', '3196.0', '66.40', '0.37', '1917.6', '39.84', '0.22', '1155.6', '24.90', '0.14', '76'),
('100', '3302.2', '69.72', '0.37', '1651.1', '34.86', '0.22', '1155.6', '24.90', '0.14', '76'),
('101', '2935.8', '59.76', '0.37', '1174.3', '23.90', '0.22', '1155.6', '24.90', '0.14', '76'),
('102', '3042.0', '63.08', '0.37', '1521.0', '31.54', '0.22', '1155.6', '24.90', '0.14', '76'),
('103', '3013.1', '61.42', '0.37', '1506.5', '30.71', '0.22', '1540.8', '33.20', '0.14', '76'),
('104', '3119.3', '64.74', '0.37', '1871.5', '38.84', '0.22', '1540.8', '33.20', '0.14', '76'),
('105', '3422.0', '69.72', '0.37', '1711.0', '34.86', '0.22', '1540.8', '33.20', '0.14', '76'),
('106', '3374.0', '69.72', '0.37', '2024.4', '41.83', '0.22', '1155.6', '24.90', '0.14', '76'),
('107', '3533.3', '74.70', '0.37', '1766.6', '37.35', '0.22', '1155.6', '24.90', '0.14', '76'),
('108', '3113.8', '63.08', '0.37', '1245.5', '25.23', '0.22', '1155.6', '24.90', '0.14', '76'),
('109', '3220.0', '66.40', '0.37', '1610.0', '33.20', '0.22', '1155.6', '24.90', '0.14', '76'),
('110', '3039.0', '61.42', '0.37', '1519.5', '30.71', '0.22', '1540.8', '33.20', '0.14', '76'),
('111', '3198.3', '66.40', '0.37', '1918.9', '39.84', '0.22', '1540.8', '33.20', '0.14', '76'),
('112', '3447.9', '69.72', '0.37', '1723.9', '34.86', '0.22', '1540.8', '33.20', '0.14', '76'),
('113', '3447.2', '72.94', '0.37', '2413.0', '51.03', '0.22', '1155.6', '24.80', '0.14', '76'),
('114', '3293.2', '69.62', '0.37', '1646.6', '34.76', '0.22', '1155.6', '24.80', '0.14', '76'),
('115', '3359.9', '67.96', '0.37', '2687.9', '54.35', '0.22', '1540.8', '33.10', '0.14', '76'),
('116', '3359.9', '67.96', '0.37', '1679.9', '33.93', '0.22', '1540.8', '33.10', '0.14', '76'),
('117', '3447.2', '72.94', '0.37', '2413.0', '51.03', '0.22', '1155.6', '24.80', '0.14', '76'),
('118', '3293.2', '69.62', '0.37', '2634.5', '55.68', '0.22', '1155.6', '24.80', '0.14', '76');

-- L2Emu Project