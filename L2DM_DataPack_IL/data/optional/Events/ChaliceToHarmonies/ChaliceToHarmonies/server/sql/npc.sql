INSERT INTO npc (`id`, `idTemplate`, `name`, `serverSideName`, `title`, `serverSideTitle`, `class`, `collision_radius`, `collision_height`, `level`, `sex`, `type`, `attackrange`, `hp`, `mp`, `hpreg`, `mpreg`, `str`, `con`, `dex`, `int`, `wit`, `men`, `exp`, `sp`, `patk`, `pdef`, `matk`, `mdef`, `atkspd`, `aggro`, `matkspd`, `rhand`, `lhand`, `armor`, `walkspd`, `runspd`, `faction_id`, `faction_range`, `isUndead`, `absorb_level`, `absorb_type`) VALUES
(57777, 25450, 'Einhasad', 1, 'Chalice to Harmonies', 1, 'Monster.archangel_50_bi', 22.00, 120.00, 79, 'male', 'L2Npc', 40, 987470, 3718, 844.90, 9.81, 60, 57, 73, 76, 70, 80, 2377821, 1136977, 9883, 4471, 14504, 1816, 409, 0, 3819, 97, 0, 0, 81, 307, '', 0, 0, 0, 'LAST_HIT');

INSERT INTO random_spawn (`groupId`, `npcId`, `count`, `initialDelay`, `respawnDelay`, `despawnDelay`, `broadcastSpawn`, `randomSpawn`) VALUES
(137, 57777, 1, -1, 1800000, 1800000, 'true', 'true');

INSERT INTO random_spawn_loc (`groupId`, `x`, `y`, `z`, `heading`) VALUES
(137, 82754, 148391, -3473, -1), #Гиран
(137, 82378, 53750, -1529, -1), #Орен
(137, 147710, -56168, -2781, -1), #Гадарт
(137, 43839, -48355, -797, -1), #Руна
(137, 147455, 27073, -2205, -1); #Аден
