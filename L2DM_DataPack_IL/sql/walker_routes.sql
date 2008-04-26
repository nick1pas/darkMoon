-- Dark Moon Dev Team
DROP TABLE IF EXISTS `walker_routes`;
CREATE TABLE `walker_routes` (
  `route_id` int(11) NOT NULL default '0',
  `npc_id` int(11) NOT NULL default '0',
  `move_point` int(9) NOT NULL default '0',
  `chatText` varchar(255) default NULL,
  `move_x` int(9) NOT NULL default '0',
  `move_y` int(9) NOT NULL default '0',
  `move_z` int(9) NOT NULL default '0',
  `delay` int(9) NOT NULL default '0',
  `running` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`route_id`,`npc_id`,`move_point`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `walker_routes` VALUES
('1', '31361', '1', null, '22418', '10249', '-3648', '61', '1'),
('1', '31361', '2', null, '23423', '11165', '-3720', '0', '1'),
('1', '31361', '3', null, '20182', '11101', '-3720', '0', '1'),
('1', '31361', '4', null, '17327', '13603', '-3728', '1', '1'),
('1', '31361', '5', 'The mass of darkness will start in a couple of days. Pay more attention to the guard!', '17410', '13038', '-3736', '10', '1'),
('1', '31361', '6', null, '20176', '12902', '-3712', '0', '1'),
('1', '31361', '7', null, '21669', '13378', '-3616', '0', '1'),
('1', '31361', '8', null, '20675', '10401', '-3712', '0', '1'),
('2', '31360', '1', null, '10826', '14777', '-4240', '62', '0'),
('2', '31360', '2', null, '10966', '14626', '-4240', '0', '0'),
('2', '31360', '3', null, '11264', '15838', '-4584', '0', '0'),
('2', '31360', '4', null, '12100', '16378', '-4590', '2', '0'),
('2', '31360', '5', 'You\'re a hard worker, Rayla!', '15088', '15664', '-4376', '6', '0'),
('2', '31360', '6', null, '15232', '16279', '-4383', '7', '0'),
('2', '31360', '7', null, '12217', '16916', '-4590', '0', '0'),
('2', '31360', '8', null, '11811', '17553', '-4580', '0', '0'),
('2', '31360', '9', null, '11275', '17628', '-4589', '0', '0'),
('2', '31360', '10', null, '10649', '17284', '-4584', '0', '0'),
('2', '31360', '11', 'You\'re a hard worker!', '7691', '18030', '-4376', '6', '0'),
('2', '31360', '12', null, '10593', '16738', '-4590', '2', '0'),
('2', '31360', '13', null, '11127', '16044', '-4589', '2', '0'),
('3', '31362', '1', 'Mr. Lid, Murdoc, and Airy! How are you doing?', '114847', '-180066', '-877', '30', '0'),
('3', '31362', '2', null, '114834', '-179685', '-877', '2', '0'),
('3', '31362', '3', null, '116122', '-179457', '-1068', '1', '0'),
('3', '31362', '4', null, '116798', '-180391', '-1200', '2', '0'),
('3', '31362', '5', null, '116324', '-181564', '-1384', '2', '0'),
('3', '31362', '6', null, '115797', '-181563', '-1336', '0', '0'),
('3', '31362', '7', 'Care to go a round?', '116054', '-181575', '-1352', '0', '0'),
('3', '31362', '8', null, '116506', '-181478', '-1384', '2', '0'),
('3', '31362', '9', null, '116634', '-180029', '-1160', '1', '0'),
('3', '31362', '10', null, '115347', '-178623', '-928', '1', '0'),
('3', '31362', '11', null, '115763', '-177591', '-888', '2', '0'),
('3', '31362', '12', 'Have a nice day, Mr. Garita and Mion!', '115801', '-177342', '-880', '1', '0'),
('3', '31362', '13', null, '115869', '-177340', '-880', '15', '0'),
('3', '31362', '14', null, '115788', '-177482', '-880', '3', '0'),
('3', '31362', '15', null, '115124', '-179821', '-885', '1', '0'),
('3', '31362', '16', null, '115103', '-180065', '-877', '1', '0'),
('4', '31363', '1', 'Where is that fool hiding?', '116731', '-182477', '-1512', '10', '1'),
('4', '31363', '2', null, '115870', '-183280', '-1472', '0', '1'),
('4', '31363', '3', null, '115746', '-183428', '-1472', '1', '1'),
('4', '31363', '4', null, '115870', '-183280', '-1472', '1', '1'),
('4', '31363', '5', null, '115999', '-183246', '-1480', '0', '1'),
('4', '31363', '6', null, '116094', '-183113', '-1480', '1', '1'),
('4', '31363', '7', 'Have you seen Torocco today?', '116584', '-184294', '-1568', '11', '1'),
('4', '31363', '8', null, '116392', '-184100', '-1560', '1', '1'),
('4', '31363', '9', null, '117093', '-182524', '-1528', '1', '1'),
('4', '31363', '10', 'Have you seen Torocco?', '117789', '-182540', '-1528', '11', '1'),
('5', '31359', '1', null, '45744', '50561', '-3065', '61', '0'),
('5', '31359', '2', null, '46444', '49742', '-3065', '2', '0'),
('5', '31359', '3', 'How can we save the Mother Tree?', '46103', '48798', '-3065', '5', '0'),
('5', '31359', '4', 'The Mother Tree is slowly dying', '45403', '48436', '-3065', '5', '0'),
('5', '31359', '5', null, '44444', '49078', '-3065', '0', '0'),
('5', '31359', '6', null, '44414', '50025', '-3065', '0', '0'),
('5', '31359', '7', null, '44957', '50568', '-3065', '0', '0'),
('5', '31359', '8', null, '44414', '50025', '-3065', '0', '0'),
('5', '31359', '9', null, '44444', '49078', '-3065', '0', '0'),
('5', '31359', '10', 'The Mother Tree is slowly dying', '45403', '48436', '-3065', '5', '0'),
('5', '31359', '11', 'How can we save the Mother Tree?', '46103', '48798', '-3065', '5', '0'),
('5', '31359', '12', null, '46444', '49742', '-3065', '2', '0'),
('6', '31358', '1', 'Lady Mirabel, may the peace of the lake be with you!', '47015', '51278', '-2992', '65', '0'),
('6', '31358', '2', null, '47437', '50441', '-2992', '0', '0'),
('6', '31358', '3', null, '47509', '49038', '-2992', '0', '0'),
('6', '31358', '4', null, '46725', '47755', '-2992', '0', '0'),
('6', '31358', '5', 'The Mother Tree is always so gorgeous!', '45319', '47339', '-2992', '5', '0'),
('6', '31358', '6', null, '43998', '47672', '-2992', '0', '0'),
('6', '31358', '7', null, '43037', '49310', '-2992', '0', '0'),
('6', '31358', '8', null, '43310', '50382', '-2992', '0', '0'),
('6', '31358', '9', null, '43896', '51060', '-2992', '0', '0'),
('6', '31358', '10', null, '43312', '50362', '-2992', '0', '0'),
('6', '31358', '11', null, '43040', '49311', '-2992', '0', '0'),
('6', '31358', '12', null, '44018', '47645', '-2992', '0', '0'),
('6', '31358', '13', 'The Mother Tree is always so gorgeous!', '45301', '47340', '-2992', '5', '0'),
('6', '31358', '14', null, '46693', '47752', '-2992', '0', '0'),
('6', '31358', '15', null, '47489', '48976', '-2992', '0', '0'),
('6', '31358', '16', null, '47441', '50455', '-2992', '0', '0'),
('7', '31357', '1', 'Where did he go?', '-86328', '241120', '-3734', '60', '0'),
('7', '31357', '2', null, '-86505', '240727', '-3704', '0', '0'),
('7', '31357', '3', null, '-86081', '240402', '-3712', '0', '0'),
('7', '31357', '4', 'Have you seen Windawood?', '-86078', '240853', '-3720', '15', '0'),
('7', '31357', '5', null, '-85957', '241389', '-3728', '0', '0'),
('7', '31357', '6', 'Where has he gone?', '-83993', '242766', '-3728', '10', '0'),
('7', '31357', '7', null, '-82952', '244461', '-3728', '0', '0'),
('7', '31357', '8', null, '-82370', '244919', '-3720', '0', '0'),
('7', '31357', '9', null, '-82129', '245020', '-3720', '0', '0'),
('7', '31357', '10', null, '-82198', '245350', '-3712', '0', '0'),
('7', '31357', '11', null, '-82554', '245137', '-3716', '1', '0'),
('7', '31357', '12', null, '-82198', '245350', '-3712', '0', '0'),
('7', '31357', '13', null, '-82129', '245020', '-3720', '0', '0'),
('7', '31357', '14', null, '-82370', '244919', '-3720', '0', '0'),
('7', '31357', '15', null, '-82952', '244461', '-3728', '0', '0'),
('7', '31357', '16', 'Where has he gone?', '-83993', '242766', '-3728', '10', '0'),
('7', '31357', '17', null, '-85957', '241389', '-3728', '0', '0'),
('7', '31357', '18', 'Have you seen Windawood?', '-86078', '240853', '-3720', '15', '0'),
('7', '31357', '19', null, '-86081', '240402', '-3712', '0', '0'),
('7', '31357', '20', null, '-86505', '240727', '-3704', '0', '0'),
('8', '31356', '1', 'A delivery for Mr. Lector? Very good!', '-81681', '243384', '-3712', '61', '1'),
('8', '31356', '2', null, '-81915', '243870', '-3712', '1', '1'),
('8', '31356', '3', null, '-82084', '243659', '-3712', '0', '1'),
('8', '31356', '4', null, '-83148', '243731', '-3728', '0', '1'),
('8', '31356', '5', 'I need a break!', '-84497', '243241', '-3728', '6', '1'),
('8', '31356', '6', null, '-85212', '243184', '-3728', '0', '1'),
('8', '31356', '7', null, '-86161', '242898', '-3728', '0', '1'),
('8', '31356', '8', null, '-86281', '242963', '-3720', '0', '1'),
('8', '31356', '9', 'Hello, Mr. Lector! Long time no see, Mr. Jackson!', '-86356', '243201', '-3720', '7', '1'),
('8', '31356', '10', null, '-86491', '242781', '-3720', '0', '1'),
('8', '31356', '11', null, '-86497', '242585', '-3728', '0', '1'),
('8', '31356', '12', null, '-86114', '241587', '-3728', '0', '1'),
('8', '31356', '13', null, '-85184', '240679', '-3728', '0', '1'),
('8', '31356', '14', 'Lulu! Lulu!', '-84099', '241264', '-3728', '6', '1'),
('8', '31356', '15', 'Lulu!', '-83709', '241238', '-3728', '0', '1'),
('8', '31356', '16', null, '-83075', '241150', '-3728', '1', '1'),
('8', '31356', '17', null, '-82383', '242926', '-3720', '1', '1'),
('9', '31364', '1', null, '-46506', '-109402', '-238', '10', '0'),
('9', '31364', '2', null, '-45752', '-111652', '-240', '0', '0'),
('9', '31364', '3', null, '-44028', '-112688', '-240', '0', '0'),
('9', '31364', '4', null, '-44049', '-114536', '-240', '0', '0'),
('9', '31364', '5', null, '-45540', '-115415', '-240', '0', '0'),
('9', '31364', '6', null, '-46526', '-117731', '-240', '0', '0'),
('9', '31364', '7', null, '-45540', '-115415', '-240', '0', '0'),
('9', '31364', '8', null, '-44049', '-114536', '-240', '0', '0'),
('9', '31364', '9', null, '-44028', '-112688', '-240', '0', '0'),
('9', '31364', '10', null, '-45752', '-111652', '-240', '0', '0'),
('10', '31365', '1', null, '-48807', '-113489', '-241', '61', '0'),
('10', '31365', '2', null, '-48821', '-113746', '-232', '1', '0'),
('10', '31365', '3', null, '-48823', '-113505', '-232', '61', '0'),
('10', '31365', '4', null, '-47339', '-113581', '-232', '1', '0'),
('10', '31365', '5', null, '-45740', '-113562', '-240', '0', '0'),
('10', '31365', '6', null, '-44851', '-112965', '-240', '0', '0'),
('10', '31365', '7', null, '-44308', '-113207', '-240', '0', '0'),
('10', '31365', '8', null, '-44204', '-113743', '-240', '0', '0'),
('10', '31365', '9', null, '-44659', '-114171', '-240', '0', '0'),
('10', '31365', '10', null, '-45167', '-114034', '-224', '0', '0'),
('10', '31365', '11', null, '-45658', '-113633', '-240', '0', '0'),
('10', '31365', '12', null, '-47363', '-113621', '-224', '0', '0'),
('10', '31365', '13', null, '-48829', '-113746', '-232', '0', '0'),
('10', '31365', '14', null, '-47363', '-113621', '-224', '0', '0'),
('10', '31365', '15', null, '-45658', '-113633', '-240', '0', '0'),
('10', '31365', '16', null, '-45167', '-114034', '-224', '0', '0'),
('10', '31365', '17', null, '-44659', '-114171', '-240', '0', '0'),
('10', '31365', '18', null, '-44204', '-113743', '-240', '0', '0'),
('10', '31365', '19', null, '-44308', '-113207', '-240', '0', '0'),
('10', '31365', '20', null, '-44851', '-112965', '-240', '0', '0'),
('10', '31365', '21', null, '-45740', '-113562', '-240', '1', '0'),
('11', '32072', '1', null, '84429', '-144065', '-1542', '10', '0'),
('11', '32072', '2', null, '84465', '-142493', '-1536', '0', '0'),
('11', '32072', '3', null, '85234', '-140963', '-1536', '0', '0'),
('11', '32072', '4', null, '87351', '-140059', '-1536', '5', '0'),
('11', '32072', '5', null, '89343', '-140746', '-1536', '0', '0'),
('11', '32072', '6', null, '90605', '-143167', '-1536', '0', '0'),
('11', '32072', '7', null, '90271', '-143869', '-1536', '10', '0'),
('11', '32072', '8', null, '90605', '-143167', '-1536', '0', '0'),
('11', '32072', '9', null, '89343', '-140746', '-1536', '0', '0'),
('11', '32072', '10', null, '87351', '-140059', '-1536', '5', '0'),
('11', '32072', '11', null, '85234', '-140963', '-1536', '0', '0'),
('11', '32072', '12', null, '84465', '-142493', '-1536', '0', '0'),
('12', '32070', '1', null, '90271', '-143869', '-1547', '10', '1'),
('12', '32070', '2', null, '90605', '-143167', '-1536', '1', '1'),
('12', '32070', '3', null, '89343', '-140746', '-1536', '1', '1'),
('12', '32070', '4', null, '87351', '-140059', '-1536', '5', '1'),
('12', '32070', '5', null, '85234', '-140963', '-1536', '1', '1'),
('12', '32070', '6', null, '84465', '-142493', '-1536', '1', '1'),
('12', '32070', '7', null, '84434', '-144061', '-1528', '10', '1'),
('12', '32070', '8', null, '84465', '-142493', '-1536', '1', '1'),
('12', '32070', '9', null, '85234', '-140963', '-1536', '1', '1'),
('12', '32070', '10', null, '87351', '-140059', '-1536', '5', '1'),
('12', '32070', '11', null, '89343', '-140746', '-1536', '1', '1'),
('12', '32070', '12', null, '90605', '-143167', '-1536', '1', '1');
