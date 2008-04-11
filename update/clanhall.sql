-- To make Clan Hall Managers only used by Clan Hall owners.
ALTER TABLE `clanhall` ADD COLUMN `id_buffer` INT NOT NULL default 0 AFTER `ownerId`;
UPDATE `clanhall` SET `id_buffer` = 30784 WHERE `id` = 1;
UPDATE `clanhall` SET `id_buffer` = 30788 WHERE `id` = 2;
UPDATE `clanhall` SET `id_buffer` = 30790 WHERE `id` = 3;
UPDATE `clanhall` SET `id_buffer` = 30786 WHERE `id` = 4;
UPDATE `clanhall` SET `id_buffer` = 30778 WHERE `id` = 5;
UPDATE `clanhall` SET `id_buffer` = 30780 WHERE `id` = 6;
UPDATE `clanhall` SET `id_buffer` = 30782 WHERE `id` = 7;
UPDATE `clanhall` SET `id_buffer` = 30774 WHERE `id` = 8;
UPDATE `clanhall` SET `id_buffer` = 30776 WHERE `id` = 9;
UPDATE `clanhall` SET `id_buffer` = 30800 WHERE `id` = 10;
UPDATE `clanhall` SET `id_buffer` = 30802 WHERE `id` = 11;
UPDATE `clanhall` SET `id_buffer` = 30798 WHERE `id` = 12;
UPDATE `clanhall` SET `id_buffer` = 35457 WHERE `id` = 13;
UPDATE `clanhall` SET `id_buffer` = 35459 WHERE `id` = 14;
UPDATE `clanhall` SET `id_buffer` = 35451 WHERE `id` = 15;
UPDATE `clanhall` SET `id_buffer` = 35455 WHERE `id` = 16;
UPDATE `clanhall` SET `id_buffer` = 35453 WHERE `id` = 17;
UPDATE `clanhall` SET `id_buffer` = 31158 WHERE `id` = 18;
UPDATE `clanhall` SET `id_buffer` = 31160 WHERE `id` = 19;
UPDATE `clanhall` SET `id_buffer` = 31156 WHERE `id` = 20;
UPDATE `clanhall` SET `id_buffer` = 31152 WHERE `id` = 21;
UPDATE `clanhall` SET `id_buffer` = 31150 WHERE `id` = 22;
UPDATE `clanhall` SET `id_buffer` = 31154 WHERE `id` = 23;
UPDATE `clanhall` SET `id_buffer` = 35467 WHERE `id` = 24;
UPDATE `clanhall` SET `id_buffer` = 35465 WHERE `id` = 25;
UPDATE `clanhall` SET `id_buffer` = 35463 WHERE `id` = 26;
UPDATE `clanhall` SET `id_buffer` = 35461 WHERE `id` = 27;

-- Using L2WareHouse (warehouse directory) indeed L2clanhallManager (default directory) until WareHouse functions works correctly.
update npc set type = 'L2Warehouse' where id IN (30784,30788,30790,30786,30778,30780,30782,30774,30776,30800,30802,30798,35457,35459,35451,35455,35453,31158,31160,31156,31152,31150,31154,35467,35465,35463,35461)

-- Ice Age Project