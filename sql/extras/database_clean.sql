-- more than ... days
SET @dt = 90;

DELETE FROM accounts WHERE DATEDIFF( CURRENT_DATE( ) , FROM_UNIXTIME( `lastactive` /1000 ) ) > @dt;

DELETE FROM accounts WHERE login NOT IN (SELECT account_name FROM characters);
DELETE FROM account_data WHERE account_name NOT IN (SELECT login FROM accounts);
DELETE FROM characters WHERE account_name NOT IN (SELECT login FROM accounts);
DELETE FROM character_friends WHERE char_id NOT IN (SELECT obj_Id FROM characters);
DELETE FROM character_hennas WHERE char_obj_id NOT IN (SELECT obj_Id FROM characters);
DELETE FROM character_macroses WHERE char_obj_id NOT IN (SELECT obj_Id FROM characters);
DELETE FROM character_quests WHERE char_id NOT IN (SELECT obj_Id FROM characters);
DELETE FROM character_recipebook WHERE char_id NOT IN (SELECT obj_Id FROM characters);
DELETE FROM character_shortcuts WHERE char_obj_id NOT IN (SELECT obj_Id FROM characters);
DELETE FROM character_skills WHERE char_obj_id NOT IN (SELECT obj_Id FROM characters);
DELETE FROM character_skills_save WHERE char_obj_id NOT IN (SELECT obj_Id FROM characters);
DELETE FROM clan_data WHERE leader_id NOT IN (SELECT obj_Id FROM characters);
DELETE FROM clan_privs WHERE clan_id NOT IN (SELECT clan_id FROM clan_data);
DELETE FROM clan_skills WHERE clan_id NOT IN (SELECT clan_id FROM clan_data);
DELETE FROM pets WHERE item_obj_id NOT IN (SELECT object_id FROM items WHERE owner_id IN (SELECT obj_Id FROM characters));
DELETE FROM items WHERE owner_id NOT IN (SELECT obj_Id FROM characters) AND owner_id NOT IN (SELECT clan_id FROM clan_data);
DELETE FROM seven_signs WHERE char_obj_id NOT IN (SELECT obj_Id FROM characters);
DELETE FROM augmentations WHERE item_id NOT IN (SELECT item_id FROM items);
UPDATE characters SET clanid=0,title="",clan_privs=0 where clanid NOT IN (SELECT clan_id FROM clan_data);
UPDATE clanhall SET ownerID=0,paidUntil=0 where ownerID NOT IN (SELECT clan_id FROM clan_data);
