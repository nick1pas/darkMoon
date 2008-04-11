-- ------------------------------
-- Table structure for custom_npc
-- ------------------------------
DROP TABLE IF EXISTS `custom_npc`;
CREATE TABLE `custom_npc` (
  `id` decimal(11,0) NOT NULL default '0',
  `idTemplate` int(11) NOT NULL default '0',
  `name` varchar(200) default NULL,
  `serverSideName` int(1) default '0',
  `title` varchar(45) default '',
  `serverSideTitle` int(1) default '0',
  `class` varchar(200) default NULL,
  `collision_radius` decimal(5,2) default NULL,
  `collision_height` decimal(5,2) default NULL,
  `level` decimal(2,0) default NULL,
  `sex` varchar(6) default NULL,
  `type` varchar(20) default NULL,
  `attackrange` int(11) default NULL,
  `hp` decimal(8,0) default NULL,
  `mp` decimal(5,0) default NULL,
  `hpreg` decimal(8,2) default NULL,
  `mpreg` decimal(5,2) default NULL,
  `str` decimal(7,0) default NULL,
  `con` decimal(7,0) default NULL,
  `dex` decimal(7,0) default NULL,
  `int` decimal(7,0) default NULL,
  `wit` decimal(7,0) default NULL,
  `men` decimal(7,0) default NULL,
  `exp` decimal(9,0) default NULL,
  `sp` decimal(8,0) default NULL,
  `patk` decimal(5,0) default NULL,
  `pdef` decimal(5,0) default NULL,
  `matk` decimal(5,0) default NULL,
  `mdef` decimal(5,0) default NULL,
  `atkspd` decimal(3,0) default NULL,
  `aggro` decimal(6,0) default NULL,
  `matkspd` decimal(4,0) default NULL,
  `rhand` decimal(4,0) default NULL,
  `lhand` decimal(4,0) default NULL,
  `armor` decimal(1,0) default NULL,
  `walkspd` decimal(3,0) default NULL,
  `runspd` decimal(3,0) default NULL,
  `faction_id` varchar(40) default NULL,
  `faction_range` decimal(4,0) default NULL,
  `isUndead` int(11) default '0',
  `absorb_level` decimal(2,0) default '0',
  `absorb_type` enum('FULL_PARTY','LAST_HIT','PARTY_ONE_RANDOM') NOT NULL default 'LAST_HIT',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- ----------------------------
-- Records for table custom_npc
-- ----------------------------

INSERT INTO `custom_npc` VALUES ('30038', '30175', 'Andromeda', '1', 'Wedding Priest Ice Age server', '1', 'NPC.a_casino_FDarkElf', '8.00', '23.00', '70', 'female', 'L2WeddingManager', '40', '3862', '1493', '11.85', '2.78', '40', '43', '30', '21', '35', '10', '5879', '590', '1314', '470', '780', '382', '278', '0', '253', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50000', '30009', 'TOFIZ', '1', 'Buffer', '1', 'NPC.a_sanctuary_teacher_MHuman', '8.00', '23.00', '70', 'male', 'L2NpcBuffer', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '999', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50001', '30009', 'Rayan', '1', 'Ice Age Test Buffer', '1', 'NPC.a_sanctuary_teacher_MHuman', '8.00', '23.00', '70', 'male', 'L2NpcBuffer', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '999', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50003', '30006', 'Luxury GateKeeper', '1', 'Tinkerbell', '1', 'NPC.a_teleporter_FHuman', '8.00', '25.00', '70', 'female', 'L2Teleporter', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '253', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50004', '30005', 'Charus', '1', 'Craft Manager', '1', 'NPC.a_warehouse_keeper_MDwarf', '8.00', '17.00', '70', 'male', 'L2CraftManager', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '253', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50005', '30009', 'Chron', '1', 'Summon Buffer Ice Age', '1', 'NPC.a_sanctuary_teacher_MHuman', '8.00', '23.00', '70', 'male', 'L2SummonBuffer', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '999', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50006', '30001', 'Gustaff', '1', 'Gold Coins Mercenary', '1', 'NPC.a_traderA_MHuman', '8.00', '22.00', '70', 'male', 'L2Merchant', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '6742', '730', '1314', '470', '780', '382', '278', '0', '253', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50007', '30009', 'Stream', '1', 'Ice Age Donator Buff', '1', 'NPC.a_traderA_MHuman', '8.00', '22.00', '70', 'male', 'L2DonatorBuffer', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '6742', '730', '1314', '470', '780', '382', '278', '0', '253', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50008', '30009', 'Nikki', '1', 'unsealer', '1', 'NPC.a_traderA_MHuman', '8.00', '22.00', '70', 'male', 'L2Merchant', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '6742', '730', '1314', '470', '780', '382', '278', '0', '253', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50009', '30009', 'Charllots', '1', 'Donator Summon Buffer', '1', 'NPC.a_sanctuary_teacher_MHuman', '8.00', '23.00', '70', 'male', 'L2DonatorPetBuffer', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '999', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50010', '30009', 'Nippo', '1', 'TvT Manager', '1', 'NPC.a_sanctuary_teacher_MHuman', '8.00', '23.00', '70', 'male', 'L2Npc', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '999', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50011', '30082', 'Trader', '1', 'Life Stone Mercenary', '1', 'NPC.a_traderA_FHuman', '8.00', '23.00', '70', 'male', 'L2Merchant', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '999', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50012', '30082', 'Newser', '1', 'Anouncer Manager Ice Age', '1', 'NPC.a_traderA_FHuman', '8.00', '23.00', '70', 'male', 'L2NpcAnnouncer', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '999', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50013', '30082', 'Gatis', '1', 'Jail Manager', '1', 'NPC.a_traderA_FHuman', '8.00', '23.00', '70', 'male', 'L2JailManager', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '999', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50014', '30082', 'Polbat', '1', 'Raid Event Manager', '1', 'NPC.a_traderA_FHuman', '8.00', '23.00', '70', 'male', 'L2EventManager', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '999', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50015', '30499', 'Visor', '1', 'Enchanter', '1', 'NPC.e_smith_master_MDwarf', '8.00', '16.50', '70', 'male', 'L2NpcEnchanter', '40', '3862', '1493', '11.85', '2.78', '40', '43', '30', '21', '20', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '333', '0', '0', '0', '55', '132', NULL, '0', '1', '0', 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50016', '31854', 'Spice', 1, 'Sexy Changer', 1, 'NPC.a_maidA_FHuman', 8, 20.5, 70, 'female', 'L2Npc', 40, 99999, 9999, NULL, NULL, 40, 43, 30, 21, 20, 10, 0, 0, 1314, 470, 780, 382, 278, 0, 333, 0, 0, 0, 55, 132, NULL, 0, 0, 0, 'LAST_HIT');
INSERT INTO `custom_npc` VALUES ('50017', '31854', 'Protector', 1, 'PVP/PK Manager', 1, 'NPC.a_maidA_FHuman', 8, 20.5, 80, 'female', 'L2Protector', 40, 99999, 9999, NULL, NULL, 40, 43, 30, 21, 20, 10, 0, 0, 1314, 470, 10000, 382, 278, 0, 3000, 0, 0, 0, 55, 132, NULL, 0, 0, 0, 'LAST_HIT');

-- L2Emu Project
-- INSERT INTO `custom_npc` VALUES ('50002', '30060', 'GM Shop', '1', 'Trader', '1', 'NPC.a_traderA_MHuman', '10.00', '24.00', '70', 'male', 'L2Merchant', '40', '3862', '1493', null, null, '40', '43', '30', '21', '35', '10', '0', '0', '1314', '470', '780', '382', '278', '0', '253', '0', '0', '0', '80', '120', null, '0', '1', '0', 'LAST_HIT');