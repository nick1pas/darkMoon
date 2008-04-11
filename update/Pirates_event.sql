-- Изменения в npc типа НиПиСия для эвента "20073_pirates"

UPDATE npc SET type = 'L2Npc' WHERE id = 31056;

--  Изменения к спаунлисту для Евента с Пиратами 20073_pirates

--  Loken
DELETE FROM spawnlist WHERE npc_templateid = 35420;
--  Ghost of Wigoth 1
DELETE FROM spawnlist WHERE npc_templateid = 31454;
--  Ghost of Wigoth 2
DELETE FROM spawnlist WHERE npc_templateid = 31452;
--  Doorman of Hell
DELETE FROM spawnlist WHERE npc_templateid = 31056;
-- Treasure Chest
DELETE FROM spawnlist WHERE npc_templateid = 18273;
DELETE FROM spawnlist WHERE npc_templateid = 18274;
DELETE FROM spawnlist WHERE npc_templateid = 18275;
DELETE FROM spawnlist WHERE npc_templateid = 18276;

-- Loken
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '35420', '41938', '208170', '-3756', '0', '0', '0', '60', '0', '0');
-- Ghost of Wigoth 1
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '31454', '43602', '212656', '-3712', '0', '0', '0', '60', '0', '0');
--  Ghost of Wigoth 2
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '31452', '51336', '206215', '-4003', '0', '0', '0', '60', '0', '0');
--  Doorman of Hell
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '31056', '52369', '218990', '-3230', '0', '0', '0', '60', '0', '0');

-- Treasure Chest
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '54262', '220114', '-3496', '0', '0', '12976', '60', '0', '0');
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '56289', '218083', '-3496', '0', '0', '12976', '60', '0', '0');
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '56286', '220096', '-3496', '0', '0', '12976', '60', '0', '0');
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '54252', '218093', '-3496', '0', '0', '12976', '60', '0', '0');
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '55296', '219099', '-3217', '0', '0', '12976', '60', '0', '0');
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '55253', '219319', '-3221', '0', '0', '12976', '60', '0', '0');
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '55487', '218884', '-3221', '0', '0', '12976', '60', '0', '0');
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '55477', '219318', '-3221', '0', '0', '12976', '60', '0', '0');
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '55474', '219091', '-3221', '0', '0', '12976', '60', '0', '0');
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '55232', '218884', '-3221', '0', '0', '12976', '60', '0', '0');
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '55046', '218883', '-3221', '0', '0', '12976', '60', '0', '0');
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '55048', '219117', '-3221', '0', '0', '12976', '60', '0', '0');
INSERT INTO `spawnlist` VALUES (NULL, '', '1', '18276', '55053', '219321', '-3221', '0', '0', '12976', '60', '0', '0');





