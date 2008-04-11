-- bosses --
delete from spawnlist where npc_templateid = 29019;
delete from spawnlist where npc_templateid = 29025;
delete from spawnlist where npc_templateid = 29066;
delete from spawnlist where npc_templateid = 29067;
delete from spawnlist where npc_templateid = 29068;
delete from spawnlist where npc_templateid = 29020;
delete from spawnlist where npc_templateid = 29028;
delete from spawnlist where npc_templateid = 29021;
delete from spawnlist where npc_templateid = 31759;
delete from spawnlist where npc_templateid = 31859;

update npc set aggro = 500 where id = 29014;
update npc set aggro = 500 where id = 29020;
update npc set aggro = 500 where id = 29021;
update npc set aggro = 500 where id = 29022;
update npc set aggro = 800 where id = 29028;

Delete From teleport where id in (20001,20002);
INSERT INTO teleport
  (Description, id, loc_x, loc_y, loc_z, price, fornoble)
VALUES
  ("Lair of Baium -> Town of Aden", 20001, 146331, 25762, -2018, 0, 0),
  ("Lair of Valakas -> Town of Goddard", 20002, 147928, -55273, -2734, 0, 0);

update npc Set `type` = "L2Teleporter" Where idTemplate = 31759;

-- Four Sepulchers --
Update npc
Set `type` = 'L2SepulcherNpc'
Where
id
in
(
31455,31456,31457,31458,31459,31460,31461,31462,31463,31464,31465,31466,31467,31468,31469,
31470,31471,31472,31473,31474,31475,31476,31477,31478,31479,31480,31481,31482,31483,31484,
31485,31486,31487,31925,31926,31927,31928,31929,31930,31931,31932,31933,31934,31935,31936,
31937,31938,31939,31940,31941,31942,31943,31944,31453,31452,31454,31919,31920,31921,31922,
31923,31924
);

Update npc
Set `type` = 'L2SepulcherMonster'
Where
id
in
(
25339,25342,25346,25349,18120,18121,18122,18123,18124,18125,18126,18127,18128,18129,18130,
18131,18132,18133,18134,18135,18136,18137,18138,18139,18140,18141,18142,18143,18144,18145,
18146,18147,18148,18149,18150,18151,18152,18153,18154,18155,18156,18157,18158,18159,18160,
18161,18162,18163,18164,18165,18166,18167,18168,18169,18170,18171,18172,18183,18184,18185,
18186,18187,18188,18189,18190,18191,18192,18193,18194,18195,18196,18197,18198,18199,18200,
18201,18202,18203,18204,18205,18206,18207,18208,18209,18210,18211,18212,18213,18214,18215,
18216,18217,18218,18219,18220,18221,18222,18223,18224,18225,18226,18227,18228,18229,18230,
18231,18232,18233,18234,18235,18236,18237,18238,18239,18240,18241,18242,18243,18244,18245,
18246,18247,18248,18249,18250,18251,18252,18253,18254,18255,18256
);

Update npc
Set `faction_range` = 600
Where
id
in
(
18120,18121,18122,18123,18124,18125,18126,18127,18128,18129,18130,18131,18132,18133,18134,
18135,18136,18137,18138,18139,18140,18141,18142,18143,18144,18145,18146,18147,18148,18149,
18150,18151,18152,18153,18154,18155,18156,18157,18158,18159,18160,18161,18162,18163,18164,
18165,18166,18167,18168,18169,18170,18171,18172,18183,18184,18185,18186,18187,18188,18189,
18190,18191,18192,18193,18194,18195,18196,18197,18198,18199,18200,18201,18202,18203,18204,
18205,18206,18207,18208,18209,18210,18211,18212,18213,18214,18215,18216,18217,18218,18219,
18220,18221,18222,18223,18224,18225,18226,18227,18228,18229,18230,18231,18232,18233,18234,
18235,18236,18237,18238,18239,18240,18241,18242,18243,18244,18245,18246,18247,18248,18249,
18250,18251,18252,18253,18254,18255,18256
);

Update npc
Set `runspd` = 0
Where
id
in
(18196,18197,18198,18199,18200,18201,18202,18203,18204,18205,18206,18207,18208,18209,18210,18211,18256);

Delete
From
	spawnlist
Where
	npc_templateid
	in
	(31452,31453,31454,31455,31456,31457,31458,31459,31460,31461,31462,31463,31464,31465,31466,31467,
	31468,31469,31470,31471,31472,31473,31474,31475,31476,31477,31478,31479,31480,31481,31482,31483,
	31484,31485,31486,31487,31919,31920,31921,31922,31923,31924,31925,31926,31927,31928,31929,31930,
	31931,31932,31933,31934,31935,31936,31937,31938,31939,31940,31941,31942,31943,31944);

Delete
From
	raidboss_spawnlist
Where
	`boss_id`
in
(25339,25342,25346,25349);

Delete
From
	spawnlist
Where
	npc_templateid
	in
	(18120,18121,18122,18123,18124,18125,18126,18127,18128,18129,18130,18131,18132,18133,18134,18135,
	18136,18137,18138,18139,18140,18141,18142,18143,18144,18145,18146,18147,18148,18149,18150,18151,
	18152,18153,18154,18155,18156,18157,18158,18159,18160,18161,18162,18163,18164,18165,18166,18167,
	18168,18169,18170,18171,18172,18183,18184,18185,18186,18187,18188,18189,18190,18191,18192,18193,
	18194,18195,18196,18197,18198,18199,18200,18201,18202,18203,18204,18205,18206,18207,18208,18209,
	18210,18211,18212,18213,18214,18215,18216,18217,18218,18219,18220,18221,18222,18223,18224,18225,
	18226,18227,18228,18229,18230,18231,18232,18233,18234,18235,18236,18237,18238,18239,18240,18241,
	18242,18243,18244,18245,18246,18247,18248,18249,18250,18251,18252,18253,18254,18255,18256);

INSERT INTO spawnlist
  (id, location, count, npc_templateid, locx, locy, locz, randomx, randomy, heading, respawn_delay, loc_id, periodOfDay)
VALUES
  (null, "", 1, 31453, 178292, -85577, -7200, 0, 0, 16256, 60, 0, 0),
  (null, "", 1, 31454, 169589, -91293, -2896, 0, 0, 16384, 60, 0, 0),
  (null, "", 1, 31920, 164304, -47600, -3507, 0, 0, -25104, 60, 0, 0),
  (null, "", 1, 31919, 170279, -88244, -2896, 0, 0, 22024, 60, 0, 0),
  (null, "", 1, 31921, 181061, -85595, -7200, 0, 0, -32584, 60, 0, 0),
  (null, "", 1, 31922, 179292, -88981, -7200, 0, 0, -33272, 60, 0, 0),
  (null, "", 1, 31923, 173202, -87004, -7200, 0, 0, -16248, 60, 0, 0),
  (null, "", 1, 31924, 175606, -82853, -7200, 0, 0, -16248, 60, 0, 0),
  (null, "", 1, 31925, 182757, -85467, -7218, 0, 0, 32631, 60, 0, 0),
  (null, "", 1, 31926, 184558, -85472, -7218, 0, 0, 32631, 60, 0, 0),
  (null, "", 1, 31927, 186372, -85464, -7218, 0, 0, 32631, 60, 0, 0),
  (null, "", 1, 31928, 188161, -85453, -7218, 0, 0, 32631, 60, 0, 0),
  (null, "", 1, 31929, 189965, -85463, -7218, 0, 0, 32631, 60, 0, 0),
  (null, "", 1, 31930, 181004, -88871, -7218, 0, 0, 32970, 60, 0, 0),
  (null, "", 1, 31931, 182852, -88861, -7218, 0, 0, 32970, 60, 0, 0),
  (null, "", 1, 31932, 184629, -88854, -7218, 0, 0, 32970, 60, 0, 0),
  (null, "", 1, 31933, 186455, -88855, -7218, 0, 0, 32970, 60, 0, 0),
  (null, "", 1, 31934, 188244, -88847, -7218, 0, 0, 32970, 60, 0, 0),
  (null, "", 1, 31935, 173099, -85123, -7218, 0, 0, 49109, 60, 0, 0),
  (null, "", 1, 31936, 173099, -83268, -7218, 0, 0, 49109, 60, 0, 0),
  (null, "", 1, 31937, 173095, -81462, -7218, 0, 0, 49109, 60, 0, 0),
  (null, "", 1, 31938, 173091, -79645, -7218, 0, 0, 49109, 60, 0, 0),
  (null, "", 1, 31939, 173078, -77859, -7217, 0, 0, 49109, 60, 0, 0),
  (null, "", 1, 31940, 175490, -81268, -7218, 0, 0, 49184, 60, 0, 0),
  (null, "", 1, 31941, 175488, -79444, -7218, 0, 0, 49184, 60, 0, 0),
  (null, "", 1, 31942, 175483, -77623, -7218, 0, 0, 49184, 60, 0, 0),
  (null, "", 1, 31943, 175479, -75847, -7218, 0, 0, 49184, 60, 0, 0),
  (null, "", 1, 31944, 175478, -74042, -7217, 0, 0, 49184, 60, 0, 0);

Update npc
set
`faction_id` = 'tomb5_clan',
`faction_range` = 600
Where
id
in
(18231,18232,18233,18234,18235,18236,18237,18238,18239,18240,18241,18242,18243);

-- Antharas --
-- Setting NPC
DELETE FROM npc WHERE idTemplate Between 29066 and 29076;
INSERT INTO npc
  (id, idTemplate, name, serverSideName, title, serverSideTitle, class, collision_radius, collision_height, level, sex, `type`, attackrange, hp, mp, hpreg, mpreg, str, con, dex, `int`, wit, men, `exp`, sp, patk, pdef, matk, mdef, atkspd, aggro, matkspd, rhand, lhand, armor, walkspd, runspd, faction_id, faction_range, isUndead, absorb_level)
VALUES
  (29066, 29066, "Antharas", 0, "", 0, "Monster.antaras", 300, 300, 79, "male", "L2Boss", 40, 11506110, 19511, 2092.02, 233.22, 60, 57, 73, 76, 70, 80, 230931687, 25593295, 11698, 7088, 24699, 1442, 333, 0, 3819, 0, 0, 0, 81, 301, "", 0, 0, 13),
  (29067, 29067, "Antharas", 0, "", 0, "Monster.antaras", 300, 300, 79, "male", "L2Boss", 40, 13090000, 22197, 2380, 265.32, 60, 57, 73, 76, 70, 80, 262720918, 29116376, 13308, 8064, 28099, 1641, 333, 0, 3819, 0, 0, 0, 81, 301, "", 0, 0, 13),
  (29068, 29068, "Antharas", 0, "", 0, "Monster.antaras", 300, 300, 79, "male", "L2Boss", 40, 14307370, 24261, 2601.34, 289.99, 60, 57, 73, 76, 70, 80, 287153963, 31824199, 14546, 8814, 30712, 1794, 333, 0, 3819, 0, 0, 0, 81, 301, "", 0, 0, 13),
  (29069, 29069, "Behemoth Dragon", 0, "", 0, "Monster3.antaras_ex_a", 63, 54, 78, "male", "L2Monster", 40, 400000, 9999, 13.43, 3.09, 40, 43, 30, 21, 20, 10, 0, 0, 9000, 5000, 6000, 6000, 300, 500, 333, 0, 0, 0, 88, 132, "", 0, 0, 0),
  (29070, 29070, "Dragon Bomber", 0, "", 0, "Monster3.antaras_ex_b_80p", 37, 28, 78, "male", "L2Monster", 40, 400000, 9999, 13.43, 3.09, 40, 43, 30, 21, 20, 10, 0, 0, 9000, 5000, 6000, 6000, 300, 500, 333, 0, 0, 0, 88, 132, "", 0, 0, 0),
  (29071, 29071, "Dragon Bomber", 0, "", 0, "Monster3.antaras_ex_b_80p", 37, 28, 78, "male", "L2Monster", 40, 400000, 9999, 13.43, 3.09, 40, 43, 30, 21, 20, 10, 0, 0, 9000, 5000, 6000, 6000, 300, 500, 333, 0, 0, 0, 88, 132, "", 0, 0, 0),
  (29072, 29072, "Dragon Bomber", 0, "", 0, "Monster3.antaras_ex_b_80p", 37, 28, 78, "male", "L2Monster", 40, 400000, 9999, 13.43, 3.09, 40, 43, 30, 21, 20, 10, 0, 0, 9000, 5000, 6000, 6000, 300, 500, 333, 0, 0, 0, 88, 132, "", 0, 0, 0),
  (29073, 29073, "Dragon Bomber", 0, "", 0, "Monster3.antaras_ex_b_80p", 37, 28, 78, "male", "L2Monster", 40, 400000, 9999, 13.43, 3.09, 40, 43, 30, 21, 20, 10, 0, 0, 9000, 5000, 6000, 6000, 300, 500, 333, 0, 0, 0, 88, 132, "", 0, 0, 0),
  (29074, 29074, "Dragon Bomber", 0, "", 0, "Monster3.antaras_ex_b_80p", 37, 28, 78, "male", "L2Monster", 40, 400000, 9999, 13.43, 3.09, 40, 43, 30, 21, 20, 10, 0, 0, 9000, 5000, 6000, 6000, 300, 500, 333, 0, 0, 0, 88, 132, "", 0, 0, 0),
  (29075, 29075, "Dragon Bomber", 0, "", 0, "Monster3.antaras_ex_b_80p", 37, 28, 78, "male", "L2Monster", 40, 400000, 9999, 13.43, 3.09, 40, 43, 30, 21, 20, 10, 0, 0, 9000, 5000, 6000, 6000, 300, 500, 333, 0, 0, 0, 88, 132, "", 0, 0, 0),
  (29076, 29076, "Dragon Bomber", 0, "", 0, "Monster3.antaras_ex_b", 42, 34.5, 78, "male", "L2Monster", 40, 400000, 9999, 13.43, 3.09, 40, 43, 30, 21, 20, 10, 0, 0, 9000, 5000, 6000, 6000, 300, 500, 333, 0, 0, 0, 88, 132, "", 0, 0, 0);
UPDATE npc SET aggro = 800 WHERE idTemplate IN (29019,29066,29067,29068);

-- Setting Skills
DELETE FROM npcskills WHERE npcid BETWEEN 29069 AND 29076;
INSERT INTO npcskills (npcid, skillid, level) VALUES
(29069, 5095, 1),
(29069, 5096, 1),
(29070, 5097, 1),
(29071, 5097, 1),
(29072, 5097, 1),
(29073, 5097, 1),
(29074, 5097, 1),
(29075, 5097, 1),
(29076, 5094, 1);

DELETE FROM npcskills WHERE npcid IN (29019,29066,29067,29068);
INSERT INTO npcskills (npcid, skillid, level) VALUES
(29019, 4045, 1),
(29019, 4106, 1),
(29019, 4107, 1),
(29019, 4108, 1),
(29019, 4109, 1),
(29019, 4110, 1),
(29019, 4111, 1),
(29019, 4112, 1),
(29019, 4113, 1),
(29019, 4122, 1),
(29019, 4299, 1),
(29066, 4045, 1),
(29066, 4106, 1),
(29066, 4107, 1),
(29066, 4109, 1),
(29066, 4110, 1),
(29066, 4111, 1),
(29066, 4112, 1),
(29066, 4113, 1),
(29066, 4122, 1),
(29066, 4299, 1),
(29066, 5092, 1),
(29066, 5093, 1),
(29067, 4045, 1),
(29067, 4106, 1),
(29067, 4107, 1),
(29067, 4109, 1),
(29067, 4110, 1),
(29067, 4111, 1),
(29067, 4112, 1),
(29067, 4113, 1),
(29067, 4122, 1),
(29067, 4299, 1),
(29067, 5092, 1),
(29067, 5093, 1),
(29068, 4045, 1),
(29068, 4106, 1),
(29068, 4107, 1),
(29068, 4109, 1),
(29068, 4110, 1),
(29068, 4111, 1),
(29068, 4112, 1),
(29068, 4113, 1),
(29068, 4122, 1),
(29068, 4299, 1),
(29068, 5092, 1),
(29068, 5093, 1);

-- Setting DropList
DELETE FROM droplist WHERE mobId IN (29019,29066,29067,29068);
INSERT INTO droplist (mobId, itemId, `min`, `max`, category, chance) VALUES
(29019, 57, 14000000, 18000000, 24, 1000000),
(29019, 57, 14000000, 18000000, 25, 1000000),
(29019, 57, 14000000, 18000000, 26, 1000000),
(29019, 57, 14000000, 18000000, 27, 1000000),
(29019, 57, 14000000, 18000000, 28, 1000000),
(29019, 57, 14000000, 18000000, 29, 1000000),
(29019, 57, 9000000, 13000000, 30, 1000000),
(29019, 80, 1, 2, 0, 250000),
(29019, 81, 1, 2, 2, 250000),
(29019, 98, 1, 2, 0, 250000),
(29019, 150, 1, 2, 1, 250000),
(29019, 151, 1, 2, 2, 250000),
(29019, 164, 1, 2, 2, 250000),
(29019, 212, 1, 2, 1, 250000),
(29019, 213, 1, 2, 3, 200000),
(29019, 235, 1, 2, 1, 250000),
(29019, 236, 1, 2, 3, 200000),
(29019, 269, 1, 2, 0, 250000),
(29019, 270, 1, 2, 3, 200000),
(29019, 288, 1, 2, 1, 250000),
(29019, 289, 1, 2, 3, 200000),
(29019, 305, 1, 2, 3, 200000),
(29019, 729, 1, 19, 21, 1000000),
(29019, 1538, 1, 59, 22, 1000000),
(29019, 2500, 1, 2, 2, 250000),
(29019, 2504, 1, 2, 0, 250000),
(29019, 3936, 1, 39, 23, 1000000),
(29019, 5287, 1, 3, 9, 500000),
(29019, 5288, 1, 3, 12, 350000),
(29019, 5289, 1, 3, 14, 250000),
(29019, 5290, 1, 3, 16, 250000),
(29019, 5291, 1, 3, 15, 250000),
(29019, 5292, 1, 3, 17, 400000),
(29019, 5293, 1, 3, 11, 250000),
(29019, 5294, 1, 3, 14, 250000),
(29019, 5295, 1, 3, 16, 250000),
(29019, 5296, 1, 3, 15, 250000),
(29019, 5297, 1, 3, 9, 250000),
(29019, 5298, 1, 3, 12, 350000),
(29019, 5301, 1, 3, 11, 250000),
(29019, 5304, 1, 3, 9, 250000),
(29019, 5305, 1, 3, 12, 300000),
(29019, 5308, 1, 3, 11, 250000),
(29019, 5311, 1, 3, 10, 350000),
(29019, 5312, 1, 3, 14, 250000),
(29019, 5313, 1, 3, 16, 250000),
(29019, 5314, 1, 3, 15, 250000),
(29019, 5315, 1, 3, 17, 400000),
(29019, 5316, 1, 3, 11, 80000),
(29019, 5317, 1, 3, 14, 250000),
(29019, 5318, 1, 3, 16, 250000),
(29019, 5319, 1, 3, 15, 250000),
(29019, 5320, 1, 3, 10, 350000),
(29019, 5323, 1, 3, 11, 80000),
(29019, 5326, 1, 3, 10, 300000),
(29019, 5329, 1, 3, 11, 90000),
(29019, 6323, 1, 5, 7, 300000),
(29019, 6324, 1, 5, 7, 350000),
(29019, 6325, 1, 5, 7, 350000),
(29019, 6326, 1, 5, 6, 300000),
(29019, 6327, 1, 5, 6, 350000),
(29019, 6328, 1, 5, 6, 350000),
(29019, 6364, 1, 1, 4, 170000),
(29019, 6365, 1, 1, 4, 170000),
(29019, 6366, 1, 1, 5, 200000),
(29019, 6367, 1, 1, 5, 200000),
(29019, 6369, 1, 1, 4, 170000),
(29019, 6370, 1, 1, 4, 170000),
(29019, 6371, 1, 1, 5, 200000),
(29019, 6372, 1, 1, 4, 160000),
(29019, 6579, 1, 1, 5, 200000),
(29019, 6580, 1, 1, 4, 160000),
(29019, 6656, 1, 1, 31, 1000000),
(29019, 6674, 1, 1, 13, 150000),
(29019, 6675, 1, 1, 13, 150000),
(29019, 6676, 1, 1, 18, 340000),
(29019, 6677, 1, 1, 19, 340000),
(29019, 6678, 1, 1, 17, 200000),
(29019, 6679, 1, 1, 20, 340000),
(29019, 6680, 1, 1, 13, 350000),
(29019, 6681, 1, 1, 18, 330000),
(29019, 6682, 1, 1, 19, 330000),
(29019, 6683, 1, 1, 20, 330000),
(29019, 6684, 1, 1, 13, 350000),
(29019, 6685, 1, 1, 18, 330000),
(29019, 6686, 1, 1, 19, 330000),
(29019, 6687, 1, 1, 20, 330000),
(29019, 6724, 1, 1, 8, 350000),
(29019, 6725, 1, 1, 8, 350000),
(29019, 6726, 1, 1, 8, 300000),
(29019, 7575, 1, 1, 5, 200000),
(29019, 8751, 2, 4, 200, 166666),
(29019, 8752, 3, 4, 200, 540000);
INSERT INTO droplist (mobId, itemId, `min`, `max`, category, chance) VALUES
(29066, 57, 14000000, 18000000, 24, 1000000),
(29066, 57, 14000000, 18000000, 25, 1000000),
(29066, 57, 14000000, 18000000, 26, 1000000),
(29066, 57, 14000000, 18000000, 27, 1000000),
(29066, 57, 14000000, 18000000, 28, 1000000),
(29066, 57, 14000000, 18000000, 29, 1000000),
(29066, 57, 9000000, 13000000, 30, 1000000),
(29066, 80, 1, 2, 0, 250000),
(29066, 81, 1, 2, 2, 250000),
(29066, 98, 1, 2, 0, 250000),
(29066, 150, 1, 2, 1, 250000),
(29066, 151, 1, 2, 2, 250000),
(29066, 164, 1, 2, 2, 250000),
(29066, 212, 1, 2, 1, 250000),
(29066, 213, 1, 2, 3, 200000),
(29066, 235, 1, 2, 1, 250000),
(29066, 236, 1, 2, 3, 200000),
(29066, 269, 1, 2, 0, 250000),
(29066, 270, 1, 2, 3, 200000),
(29066, 288, 1, 2, 1, 250000),
(29066, 289, 1, 2, 3, 200000),
(29066, 305, 1, 2, 3, 200000),
(29066, 729, 1, 19, 21, 1000000),
(29066, 1538, 1, 59, 22, 1000000),
(29066, 2500, 1, 2, 2, 250000),
(29066, 2504, 1, 2, 0, 250000),
(29066, 3936, 1, 39, 23, 1000000),
(29066, 5287, 1, 3, 9, 500000),
(29066, 5288, 1, 3, 12, 350000),
(29066, 5289, 1, 3, 14, 250000),
(29066, 5290, 1, 3, 16, 250000),
(29066, 5291, 1, 3, 15, 250000),
(29066, 5292, 1, 3, 17, 400000),
(29066, 5293, 1, 3, 11, 250000),
(29066, 5294, 1, 3, 14, 250000),
(29066, 5295, 1, 3, 16, 250000),
(29066, 5296, 1, 3, 15, 250000),
(29066, 5297, 1, 3, 9, 250000),
(29066, 5298, 1, 3, 12, 350000),
(29066, 5301, 1, 3, 11, 250000),
(29066, 5304, 1, 3, 9, 250000),
(29066, 5305, 1, 3, 12, 300000),
(29066, 5308, 1, 3, 11, 250000),
(29066, 5311, 1, 3, 10, 350000),
(29066, 5312, 1, 3, 14, 250000),
(29066, 5313, 1, 3, 16, 250000),
(29066, 5314, 1, 3, 15, 250000),
(29066, 5315, 1, 3, 17, 400000),
(29066, 5316, 1, 3, 11, 80000),
(29066, 5317, 1, 3, 14, 250000),
(29066, 5318, 1, 3, 16, 250000),
(29066, 5319, 1, 3, 15, 250000),
(29066, 5320, 1, 3, 10, 350000),
(29066, 5323, 1, 3, 11, 80000),
(29066, 5326, 1, 3, 10, 300000),
(29066, 5329, 1, 3, 11, 90000),
(29066, 6323, 1, 5, 7, 300000),
(29066, 6324, 1, 5, 7, 350000),
(29066, 6325, 1, 5, 7, 350000),
(29066, 6326, 1, 5, 6, 300000),
(29066, 6327, 1, 5, 6, 350000),
(29066, 6328, 1, 5, 6, 350000),
(29066, 6364, 1, 1, 4, 170000),
(29066, 6365, 1, 1, 4, 170000),
(29066, 6366, 1, 1, 5, 200000),
(29066, 6367, 1, 1, 5, 200000),
(29066, 6369, 1, 1, 4, 170000),
(29066, 6370, 1, 1, 4, 170000),
(29066, 6371, 1, 1, 5, 200000),
(29066, 6372, 1, 1, 4, 160000),
(29066, 6579, 1, 1, 5, 200000),
(29066, 6580, 1, 1, 4, 160000),
(29066, 6656, 1, 1, 31, 1000000),
(29066, 6674, 1, 1, 13, 150000),
(29066, 6675, 1, 1, 13, 150000),
(29066, 6676, 1, 1, 18, 340000),
(29066, 6677, 1, 1, 19, 340000),
(29066, 6678, 1, 1, 17, 200000),
(29066, 6679, 1, 1, 20, 340000),
(29066, 6680, 1, 1, 13, 350000),
(29066, 6681, 1, 1, 18, 330000),
(29066, 6682, 1, 1, 19, 330000),
(29066, 6683, 1, 1, 20, 330000),
(29066, 6684, 1, 1, 13, 350000),
(29066, 6685, 1, 1, 18, 330000),
(29066, 6686, 1, 1, 19, 330000),
(29066, 6687, 1, 1, 20, 330000),
(29066, 6724, 1, 1, 8, 350000),
(29066, 6725, 1, 1, 8, 350000),
(29066, 6726, 1, 1, 8, 300000),
(29066, 7575, 1, 1, 5, 200000),
(29066, 8751, 2, 4, 200, 166666),
(29066, 8752, 3, 4, 200, 540000);
INSERT INTO droplist (mobId, itemId, `min`, `max`, category, chance) VALUES
(29067, 57, 14000000, 18000000, 24, 1000000),
(29067, 57, 14000000, 18000000, 25, 1000000),
(29067, 57, 14000000, 18000000, 26, 1000000),
(29067, 57, 14000000, 18000000, 27, 1000000),
(29067, 57, 14000000, 18000000, 28, 1000000),
(29067, 57, 14000000, 18000000, 29, 1000000),
(29067, 57, 9000000, 13000000, 30, 1000000),
(29067, 80, 1, 2, 0, 250000),
(29067, 81, 1, 2, 2, 250000),
(29067, 98, 1, 2, 0, 250000),
(29067, 150, 1, 2, 1, 250000),
(29067, 151, 1, 2, 2, 250000),
(29067, 164, 1, 2, 2, 250000),
(29067, 212, 1, 2, 1, 250000),
(29067, 213, 1, 2, 3, 200000),
(29067, 235, 1, 2, 1, 250000),
(29067, 236, 1, 2, 3, 200000),
(29067, 269, 1, 2, 0, 250000),
(29067, 270, 1, 2, 3, 200000),
(29067, 288, 1, 2, 1, 250000),
(29067, 289, 1, 2, 3, 200000),
(29067, 305, 1, 2, 3, 200000),
(29067, 729, 1, 19, 21, 1000000),
(29067, 1538, 1, 59, 22, 1000000),
(29067, 2500, 1, 2, 2, 250000),
(29067, 2504, 1, 2, 0, 250000),
(29067, 3936, 1, 39, 23, 1000000),
(29067, 5287, 1, 3, 9, 500000),
(29067, 5288, 1, 3, 12, 350000),
(29067, 5289, 1, 3, 14, 250000),
(29067, 5290, 1, 3, 16, 250000),
(29067, 5291, 1, 3, 15, 250000),
(29067, 5292, 1, 3, 17, 400000),
(29067, 5293, 1, 3, 11, 250000),
(29067, 5294, 1, 3, 14, 250000),
(29067, 5295, 1, 3, 16, 250000),
(29067, 5296, 1, 3, 15, 250000),
(29067, 5297, 1, 3, 9, 250000),
(29067, 5298, 1, 3, 12, 350000),
(29067, 5301, 1, 3, 11, 250000),
(29067, 5304, 1, 3, 9, 250000),
(29067, 5305, 1, 3, 12, 300000),
(29067, 5308, 1, 3, 11, 250000),
(29067, 5311, 1, 3, 10, 350000),
(29067, 5312, 1, 3, 14, 250000),
(29067, 5313, 1, 3, 16, 250000),
(29067, 5314, 1, 3, 15, 250000),
(29067, 5315, 1, 3, 17, 400000),
(29067, 5316, 1, 3, 11, 80000),
(29067, 5317, 1, 3, 14, 250000),
(29067, 5318, 1, 3, 16, 250000),
(29067, 5319, 1, 3, 15, 250000),
(29067, 5320, 1, 3, 10, 350000),
(29067, 5323, 1, 3, 11, 80000),
(29067, 5326, 1, 3, 10, 300000),
(29067, 5329, 1, 3, 11, 90000),
(29067, 6323, 1, 5, 7, 300000),
(29067, 6324, 1, 5, 7, 350000),
(29067, 6325, 1, 5, 7, 350000),
(29067, 6326, 1, 5, 6, 300000),
(29067, 6327, 1, 5, 6, 350000),
(29067, 6328, 1, 5, 6, 350000),
(29067, 6364, 1, 1, 4, 170000),
(29067, 6365, 1, 1, 4, 170000),
(29067, 6366, 1, 1, 5, 200000),
(29067, 6367, 1, 1, 5, 200000),
(29067, 6369, 1, 1, 4, 170000),
(29067, 6370, 1, 1, 4, 170000),
(29067, 6371, 1, 1, 5, 200000),
(29067, 6372, 1, 1, 4, 160000),
(29067, 6579, 1, 1, 5, 200000),
(29067, 6580, 1, 1, 4, 160000),
(29067, 6656, 1, 1, 31, 1000000),
(29067, 6674, 1, 1, 13, 150000),
(29067, 6675, 1, 1, 13, 150000),
(29067, 6676, 1, 1, 18, 340000),
(29067, 6677, 1, 1, 19, 340000),
(29067, 6678, 1, 1, 17, 200000),
(29067, 6679, 1, 1, 20, 340000),
(29067, 6680, 1, 1, 13, 350000),
(29067, 6681, 1, 1, 18, 330000),
(29067, 6682, 1, 1, 19, 330000),
(29067, 6683, 1, 1, 20, 330000),
(29067, 6684, 1, 1, 13, 350000),
(29067, 6685, 1, 1, 18, 330000),
(29067, 6686, 1, 1, 19, 330000),
(29067, 6687, 1, 1, 20, 330000),
(29067, 6724, 1, 1, 8, 350000),
(29067, 6725, 1, 1, 8, 350000),
(29067, 6726, 1, 1, 8, 300000),
(29067, 7575, 1, 1, 5, 200000),
(29067, 8751, 2, 4, 200, 166666),
(29067, 8752, 3, 4, 200, 540000);
INSERT INTO droplist (mobId, itemId, `min`, `max`, category, chance) VALUES
(29068, 57, 14000000, 18000000, 24, 1000000),
(29068, 57, 14000000, 18000000, 25, 1000000),
(29068, 57, 14000000, 18000000, 26, 1000000),
(29068, 57, 14000000, 18000000, 27, 1000000),
(29068, 57, 14000000, 18000000, 28, 1000000),
(29068, 57, 14000000, 18000000, 29, 1000000),
(29068, 57, 9000000, 13000000, 30, 1000000),
(29068, 80, 1, 2, 0, 250000),
(29068, 81, 1, 2, 2, 250000),
(29068, 98, 1, 2, 0, 250000),
(29068, 150, 1, 2, 1, 250000),
(29068, 151, 1, 2, 2, 250000),
(29068, 164, 1, 2, 2, 250000),
(29068, 212, 1, 2, 1, 250000),
(29068, 213, 1, 2, 3, 200000),
(29068, 235, 1, 2, 1, 250000),
(29068, 236, 1, 2, 3, 200000),
(29068, 269, 1, 2, 0, 250000),
(29068, 270, 1, 2, 3, 200000),
(29068, 288, 1, 2, 1, 250000),
(29068, 289, 1, 2, 3, 200000),
(29068, 305, 1, 2, 3, 200000),
(29068, 729, 1, 19, 21, 1000000),
(29068, 1538, 1, 59, 22, 1000000),
(29068, 2500, 1, 2, 2, 250000),
(29068, 2504, 1, 2, 0, 250000),
(29068, 3936, 1, 39, 23, 1000000),
(29068, 5287, 1, 3, 9, 500000),
(29068, 5288, 1, 3, 12, 350000),
(29068, 5289, 1, 3, 14, 250000),
(29068, 5290, 1, 3, 16, 250000),
(29068, 5291, 1, 3, 15, 250000),
(29068, 5292, 1, 3, 17, 400000),
(29068, 5293, 1, 3, 11, 250000),
(29068, 5294, 1, 3, 14, 250000),
(29068, 5295, 1, 3, 16, 250000),
(29068, 5296, 1, 3, 15, 250000),
(29068, 5297, 1, 3, 9, 250000),
(29068, 5298, 1, 3, 12, 350000),
(29068, 5301, 1, 3, 11, 250000),
(29068, 5304, 1, 3, 9, 250000),
(29068, 5305, 1, 3, 12, 300000),
(29068, 5308, 1, 3, 11, 250000),
(29068, 5311, 1, 3, 10, 350000),
(29068, 5312, 1, 3, 14, 250000),
(29068, 5313, 1, 3, 16, 250000),
(29068, 5314, 1, 3, 15, 250000),
(29068, 5315, 1, 3, 17, 400000),
(29068, 5316, 1, 3, 11, 80000),
(29068, 5317, 1, 3, 14, 250000),
(29068, 5318, 1, 3, 16, 250000),
(29068, 5319, 1, 3, 15, 250000),
(29068, 5320, 1, 3, 10, 350000),
(29068, 5323, 1, 3, 11, 80000),
(29068, 5326, 1, 3, 10, 300000),
(29068, 5329, 1, 3, 11, 90000),
(29068, 6323, 1, 5, 7, 300000),
(29068, 6324, 1, 5, 7, 350000),
(29068, 6325, 1, 5, 7, 350000),
(29068, 6326, 1, 5, 6, 300000),
(29068, 6327, 1, 5, 6, 350000),
(29068, 6328, 1, 5, 6, 350000),
(29068, 6364, 1, 1, 4, 170000),
(29068, 6365, 1, 1, 4, 170000),
(29068, 6366, 1, 1, 5, 200000),
(29068, 6367, 1, 1, 5, 200000),
(29068, 6369, 1, 1, 4, 170000),
(29068, 6370, 1, 1, 4, 170000),
(29068, 6371, 1, 1, 5, 200000),
(29068, 6372, 1, 1, 4, 160000),
(29068, 6579, 1, 1, 5, 200000),
(29068, 6580, 1, 1, 4, 160000),
(29068, 6656, 1, 1, 31, 1000000),
(29068, 6674, 1, 1, 13, 150000),
(29068, 6675, 1, 1, 13, 150000),
(29068, 6676, 1, 1, 18, 340000),
(29068, 6677, 1, 1, 19, 340000),
(29068, 6678, 1, 1, 17, 200000),
(29068, 6679, 1, 1, 20, 340000),
(29068, 6680, 1, 1, 13, 350000),
(29068, 6681, 1, 1, 18, 330000),
(29068, 6682, 1, 1, 19, 330000),
(29068, 6683, 1, 1, 20, 330000),
(29068, 6684, 1, 1, 13, 350000),
(29068, 6685, 1, 1, 18, 330000),
(29068, 6686, 1, 1, 19, 330000),
(29068, 6687, 1, 1, 20, 330000),
(29068, 6724, 1, 1, 8, 350000),
(29068, 6725, 1, 1, 8, 350000),
(29068, 6726, 1, 1, 8, 300000),
(29068, 7575, 1, 1, 5, 200000),
(29068, 8751, 2, 4, 200, 166666),
(29068, 8752, 3, 4, 200, 540000);
DELETE FROM droplist WHERE mobId = 29069;
INSERT INTO droplist (mobId, itemId, `min`, `max`, category, chance) VALUES
(29069, 8600, 4, 36, 0, 700000),
(29069, 8601, 4, 36, 1, 700000),
(29069, 8602, 4, 36, 2, 700000),
(29069, 8603, 4, 36, 3, 700000),
(29069, 8604, 4, 36, 4, 700000),
(29069, 8605, 4, 36, 5, 700000),
(29069, 8606, 4, 36, 6, 700000),
(29069, 8607, 4, 36, 7, 700000),
(29069, 8608, 4, 36, 8, 700000),
(29069, 8609, 4, 36, 9, 700000),
(29069, 8610, 4, 36, 10, 700000),
(29069, 8611, 4, 36, 11, 700000),
(29069, 8612, 4, 36, 12, 700000),
(29069, 8613, 4, 36, 13, 700000),
(29069, 8614, 4, 36, 14, 700000);

-- Update Teleport
Update npc set `type` = 'L2Teleporter' Where idTemplate = 31859;
Delete From teleport where id in (20000);
INSERT INTO teleport (Description, id, loc_x, loc_y, loc_z, price, fornoble) VALUES ("Lair of Antharas -> Toen of Giran", 20000, 83400, 147943, -3404, 0, 0);

-- Sailren
-- Update Velociraptor
Delete From minions Where boss_id = 22196;
Delete From minions Where boss_id = 22218;
INSERT INTO minions (boss_id, minion_id, amount_min, amount_max) VALUES (22196, 22197, 2, 2);
INSERT INTO minions (boss_id, minion_id, amount_min, amount_max) VALUES (22218, 22197, 2, 2);

-- Update Sailren
Update npc set `type` = 'L2RaidBoss' Where idTemplate = 29065;
Update npc set aggro = 500 Where idTemplate = 29065;

-- Update Teleport
Update npc set `type` = 'L2Teleporter' Where idTemplate = 32107;
Delete From teleport where id in (20004);
INSERT INTO teleport (Description, id, loc_x, loc_y, loc_z, price, fornoble) VALUES ("Lair of Sailren -> Primeval Isle Wharf", 20004, 10468, -24569, -3650, 0, 0);

-- Update Spawnlist
Delete From spawnlist where npc_templateid in (32109);
INSERT INTO spawnlist
  (id, location, count, npc_templateid, locx, locy, locz, randomx, randomy, heading, respawn_delay, loc_id, periodOfDay)
VALUES
  (null, "primeval_isle", 1, 32109, 23666, -7144, -1134, 0, 0, 46433, 60, 0, 0);

-- Van Halter
-- Update Npc
Update npc set rhand = 8208, aggro = 0 where id = 29062;
Update npc set rhand = 8208, aggro = 0 where id = 29063;
Update npc set rhand = 8207, lhand = 8207, aggro = 0 where id = 29064;
Update npc set faction_id = "VANHALTER", faction_range = 1800 where id in (22191,22192,22193,29062);
Update npc set `type` = "L2Monster" , walkspd = 0 ,runspd = 0, aggro = 500 where id in (32051,32058,32059,32060,32061,32062,32063,32064,32065,32066,32067,32068);

-- Update minions
Delete From minions where boss_id in (29062,22188,22191);
INSERT INTO minions
  (boss_id, minion_id, amount_min, amount_max)
VALUES
  ("29062", "29063", "1", "1"),
  ("29062", "29064", "3", "3"),
  ("22188", "22189", "4", "4"),
  ("22188", "22190", "1", "1"),
  ("22191", "22192", "1", "1"),
  ("22191", "22193", "1", "1");

-- Update spawnlist
Delete From raidboss_spawnlist where boss_id = 29062;
Delete From spawnlist where npc_templateId in
(22175,22176,22188,22189,22190,22191,22192,22193,22195,29062,29063,29064,32038,32051,32058,32059,32060,32061,32062,32063,32064,32065,32066,32067,32068);

-- L2Emu Project