-- Shilen's Temple Dev Team
CREATE TABLE `custom_spawnlist` (
  `id` int(11) NOT NULL auto_increment,
  `location` varchar(40) default NULL,
  `count` int(9) NOT NULL default '0',
  `npc_templateid` int(9) NOT NULL default '0',
  `locx` int(9) NOT NULL default '0',
  `locy` int(9) NOT NULL default '0',
  `locz` int(9) NOT NULL default '0',
  `randomx` int(9) NOT NULL default '0',
  `randomy` int(9) NOT NULL default '0',
  `heading` int(9) NOT NULL default '0',
  `respawn_delay` int(9) NOT NULL default '0',
  `loc_id` int(9) NOT NULL default '0',
  `periodOfDay` decimal(2,0) default '0',
  PRIMARY KEY  (`id`),
  KEY `key_npc_templateid` (`npc_templateid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `custom_spawnlist` VALUES
('1', 'Giran Temple-Wedding Priest', '1', '30038', '85276', '148403', '-3402', '0', '0', '61002', '60', '0', '0'),
('2', 'luxury_gatekeeper', '1', '50003', '82739', '147829', '-3469', '0', '0', '25320', '60', '0', '0'),
('3', 'luxury_gatekeeper', '1', '50003', '-81617', '150245', '-3129', '0', '0', '16059', '60', '0', '0'),
('4', 'luxury_gatekeeper', '1', '50003', '22075', '160330', '-2691', '0', '0', '35808', '60', '0', '0'),
('5', 'luxury_gatekeeper', '1', '50003', '82179', '36979', '-2333', '0', '0', '12516', '60', '0', '0'),
('6', 'luxury_gatekeeper', '1', '50003', '116099', '144859', '-2606', '0', '0', '6712', '60', '0', '0'),
('7', 'luxury_gatekeeper', '1', '50003', '119497', '158731', '-3776', '0', '0', '48247', '60', '0', '0'),
('8', 'luxury_gatekeeper', '1', '50003', '115482', '-179701', '-909', '0', '0', '19084', '60', '0', '0'),
('9', 'luxury_gatekeeper', '1', '50003', '10550', '17421', '-4611', '0', '0', '47475', '60', '0', '0'),
('10', 'luxury_gatekeeper', '1', '50003', '47814', '48638', '-3022', '0', '0', '27932', '60', '0', '0'),
('11', 'luxury_gatekeeper', '1', '50003', '-45970', '-111591', '-265', '0', '0', '64792', '60', '0', '0'),
('12', 'luxury_gatekeeper', '1', '50003', '-84518', '241674', '-3755', '0', '0', '34283', '60', '0', '0'),
('13', 'luxury_gatekeeper', '1', '50003', '18026', '145293', '-3075', '0', '0', '6712', '60', '0', '0'),
('14', 'luxury_gatekeeper', '1', '50003', '17245', '169873', '-3525', '0', '0', '14326', '60', '0', '0'),
('15', 'luxury_gatekeeper', '1', '50003', '-14546', '124463', '-3147', '0', '0', '62852', '60', '0', '0'),
('16', 'luxury_gatekeeper', '1', '50003', '115890', '75048', '-2625', '0', '0', '23096', '60', '0', '0'),
('17', 'luxury_gatekeeper', '1', '50003', '146532', '28547', '-2295', '0', '0', '56795', '60', '0', '0'),
('18', 'luxury_gatekeeper', '1', '50003', '108415', '219272', '-3701', '0', '0', '41760', '60', '0', '0'),
('19', 'luxury_gatekeeper', '1', '50003', '82884', '55404', '-1551', '0', '0', '15911', '60', '0', '0'),
('20', 'luxury_gatekeeper', '1', '50003', '115871', '249203', '-813', '0', '0', '59899', '60', '0', '0'),
('21', 'luxury_gatekeeper', '1', '50003', '147460', '-55255', '-2734', '0', '0', '56189', '60', '0', '0'),
('22', 'Giran_CraferManager', '1', '50004', '82222', '148590', '-3468', '0', '0', '62817', '60', '0', '0'),
('23', 'classmaster_TI', '1', '31255', '-84466', '243171', '-3729', '0', '0', '21921', '15', '0', '0'),
('24', 'classmaster_E', '1', '31255', '45036', '48384', '-3060', '0', '0', '11274', '15', '0', '0'),
('25', 'classmaster_DE', '1', '31255', '11283', '15951', '-4584', '0', '0', '13344', '15', '0', '0'),
('26', 'classmaster_DW', '1', '31255', '115774', '-178666', '-958', '0', '0', '31430', '15', '0', '0'),
('27', 'classmaster_ORC', '1', '31255', '-44747', '-113865', '-208', '0', '0', '46178', '15', '0', '0'),
('28', 'classmaster_giran', '1', '31255', '83624', '148859', '-3405', '0', '0', '62817', '60', '0', '0'),
('29', 'gmshop_npc', '1', '50002', '9829', '16339', '-4540', '0', '0', '60764', '60', '0', '0'),
('30', 'gmshop_npc', '1', '50002', '114711', '-178361', '-820', '0', '0', '3969', '60', '0', '0'),
('31', 'gmshop_npc', '1', '50002', '45860', '49840', '-3060', '0', '0', '6028', '60', '0', '0'),
('32', 'gmshop_npc', '1', '50002', '-44810', '-113482', '-199', '0', '0', '15888', '60', '0', '0'),
('33', 'gmshop_npc', '1', '50002', '-84815', '244478', '-3730', '0', '0', '0', '60', '0', '0'),
('34', 'gmshop_npc', '1', '50002', '83183', '147912', '-3405', '0', '0', '34589', '60', '0', '0'),
('35', 'gmshop_npc', '1', '50002', '16537', '142714', '-2706', '0', '0', '21799', '60', '0', '0'),
('36', 'gmshop_npc', '1', '50002', '17668', '170231', '-3509', '0', '0', '946', '60', '0', '0'),
('37', 'gmshop_npc', '1', '50002', '-81029', '150886', '-3044', '0', '0', '25377', '60', '0', '0'),
('38', 'gmshop_npc', '1', '50002', '-12310', '122547', '-3102', '0', '0', '24576', '60', '0', '0'),
('39', 'gmshop_npc', '1', '50002', '117140', '75825', '-2730', '0', '0', '22813', '60', '0', '0'),
('40', 'gmshop_npc', '1', '50002', '147446', '26004', '-2013', '0', '0', '16384', '60', '0', '0'),
('41', 'gmshop_npc', '1', '50002', '83166', '53414', '-1454', '0', '0', '34065', '60', '0', '0'),
('42', 'gmshop_npc', '1', '50002', '107577', '218107', '-3675', '0', '0', '27932', '60', '0', '0'),
('43', 'gmshop_npc', '1', '50002', '22205', '159230', '-2476', '0', '0', '30567', '60', '0', '0'),
('44', 'gmshop_npc', '1', '50002', '22177', '160806', '-2676', '0', '0', '47997', '60', '0', '0'),
('45', 'gmshop_npc', '1', '50002', '82369', '36985', '-2333', '0', '0', '10251', '60', '0', '0'),
('46', 'gmshop_npc', '1', '50002', '115402', '144965', '-2349', '0', '0', '18443', '60', '0', '0'),
('47', 'gmshop_npc', '1', '50002', '116287', '144880', '-2606', '0', '0', '18443', '60', '0', '0'),
('48', 'gmshop_npc', '1', '50002', '119279', '158680', '-3774', '0', '0', '49940', '60', '0', '0'),
('49', 'gmshop_npc', '1', '50002', '115797', '248900', '-829', '0', '0', '6134', '60', '0', '0'),
('50', 'gmshop_npc', '1', '50002', '147868', '-55294', '-2734', '0', '0', '47671', '60', '0', '0'),
('51', 'playing_with_fire_event_manager', '1', '31230', '-13073', '122841', '-3117', '0', '0', '0', '60', '0', '0'),
('52', 'playing_with_fire_event_manager', '1', '31230', '-13972', '121893', '-2988', '0', '0', '32768', '60', '0', '0'),
('53', 'playing_with_fire_event_manager', '1', '31230', '-14843', '123710', '-3117', '0', '0', '8192', '60', '0', '0'),
('54', 'playing_with_fire_event_manager', '1', '31230', '-44342', '-113726', '-240', '0', '0', '0', '60', '0', '0'),
('55', 'playing_with_fire_event_manager', '1', '31230', '-44671', '-115437', '-240', '0', '0', '22500', '60', '0', '0'),
('56', 'playing_with_fire_event_manager', '1', '31230', '-80731', '151152', '-3043', '0', '0', '28672', '60', '0', '0'),
('57', 'playing_with_fire_event_manager', '1', '31230', '-82678', '151666', '-3129', '0', '0', '49152', '60', '0', '0'),
('58', 'playing_with_fire_event_manager', '1', '31230', '-84097', '150171', '-3129', '0', '0', '4096', '60', '0', '0'),
('59', 'playing_with_fire_event_manager', '1', '31230', '114719', '-178742', '-821', '0', '0', '0', '60', '0', '0'),
('60', 'playing_with_fire_event_manager', '1', '31230', '115708', '-182422', '-1449', '0', '0', '0', '60', '0', '0'),
('61', 'playing_with_fire_event_manager', '1', '31230', '18154', '145192', '-3054', '0', '0', '7400', '60', '0', '0'),
('62', 'playing_with_fire_event_manager', '1', '31230', '19214', '144327', '-3097', '0', '0', '32768', '60', '0', '0'),
('63', 'playing_with_fire_event_manager', '1', '31230', '19459', '145775', '-3086', '0', '0', '48000', '60', '0', '0'),
('64', 'playing_with_fire_event_manager', '1', '31230', '44157', '50827', '-3059', '0', '0', '57344', '60', '0', '0'),
('65', 'playing_with_fire_event_manager', '1', '31230', '47146', '49382', '-3059', '0', '0', '32000', '60', '0', '0'),
('66', 'playing_with_fire_event_manager', '1', '31230', '81620', '148689', '-3464', '0', '0', '32768', '60', '0', '0'),
('67', 'playing_with_fire_event_manager', '1', '31230', '81691', '145610', '-3467', '0', '0', '32768', '60', '0', '0'),
('68', 'playing_with_fire_event_manager', '1', '31230', '83329', '149095', '-3405', '0', '0', '49152', '60', '0', '0'),
('69', 'glittering_medals_event_manager', '1', '31227', '-13073', '122801', '-3117', '0', '0', '60', '0', '0', '0'),
('70', 'glittering_medals_event_manager', '1', '31227', '-13949', '121934', '-2988', '0', '0', '32768', '60', '0', '0'),
('71', 'glittering_medals_event_manager', '1', '31227', '-14786', '123686', '-3117', '0', '0', '8192', '60', '0', '0'),
('72', 'glittering_medals_event_manager', '1', '31227', '-44337', '-113669', '-240', '0', '0', '60', '0', '0', '0'),
('73', 'glittering_medals_event_manager', '1', '31227', '-44628', '-115409', '-240', '0', '0', '22500', '60', '0', '0'),
('74', 'glittering_medals_event_manager', '1', '31227', '-80789', '151073', '-3043', '0', '0', '28672', '60', '0', '0'),
('75', 'glittering_medals_event_manager', '1', '31227', '-82623', '151666', '-3129', '0', '0', '49152', '60', '0', '0'),
('76', 'glittering_medals_event_manager', '1', '31227', '-84049', '150176', '-3129', '0', '0', '4096', '60', '0', '0'),
('77', 'glittering_medals_event_manager', '1', '31227', '114733', '-178691', '-821', '0', '0', '60', '0', '0', '0'),
('78', 'glittering_medals_event_manager', '1', '31227', '115708', '-182362', '-1449', '0', '0', '60', '0', '0', '0'),
('79', 'glittering_medals_event_manager', '1', '31227', '18178', '145149', '-3054', '0', '0', '7400', '60', '0', '0'),
('80', 'glittering_medals_event_manager', '1', '31227', '19208', '144380', '-3097', '0', '0', '32768', '60', '0', '0'),
('81', 'glittering_medals_event_manager', '1', '31227', '19508', '145775', '-3086', '0', '0', '48000', '60', '0', '0'),
('82', 'glittering_medals_event_manager', '1', '31227', '44122', '50784', '-3059', '0', '0', '57344', '60', '0', '0'),
('83', 'glittering_medals_event_manager', '1', '31227', '47151', '49436', '-3059', '0', '0', '32000', '60', '0', '0'),
('84', 'glittering_medals_event_manager', '1', '31227', '81621', '148725', '-3467', '0', '0', '32768', '60', '0', '0'),
('85', 'glittering_medals_event_manager', '1', '31227', '81680', '145656', '-3467', '0', '0', '32768', '60', '0', '0'),
('86', 'glittering_medals_event_manager', '1', '31227', '82188', '148742', '-3467', '0', '0', '60', '0', '0', '0'),
('87', 'glittering_medals_event_manager', '1', '31227', '83332', '149160', '-3405', '0', '0', '49152', '60', '0', '0'),
('88', 'l2day_event_manager', '1', '31774', '-13078', '122900', '-3117', '0', '0', '60', '0', '0', '0'),
('89', 'gm_consulation_service', '1', '50013', '-114470', '-249307', '-2987', '0', '0', '49152', '60', '0', '0'),
('90', 'giran_raid_event_manager', '1', '50014', '82839', '149395', '-3495', '0', '0', '49152', '60', '0', '0');