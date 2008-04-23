@echo off
title ------------------------------------ = Shilen's Temple Dev Team Database Updater = ------------------------------------
color 02
CLS
REM This is Shilen's Temple Database Updater.
REM ############################################
REM ## You can change here your own DB params ##
REM ############################################
REM MYSQL BIN PATH
REM Default values: 
set mysqlBinPath=C:\Program Files\MySQL\MySQL Server 5.1\bin\

REM LOGINSERVER
set lsuser=root
set lspass=
set lsdb=sht
set lshost=localhost

REM GS
set gsuser=root
set gspass=
set gsdb=sht
set gshost=localhost

set mysqldumpPath="%mysqlBinPath%\mysqldump"  
set mysqlPath="%mysqlBinPath%\mysql"  

echo.
echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
echo !                                  WELCOME TO                               !
echo !                               Database Updater                            !
echo !                       Developed for Shilen's Temple Server                !
echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
echo. 
echo.
echo.
echo.
echo         PLEASE SELECT YOUR INSTALL TYPE :
echo.         
echo          PRESS (U) FOR UPDATE Databases      
echo.
echo           QUIT (Q) TO EXIT THIS UPDATER  
echo.
:asklogin
set loginprompt=x
set /p loginprompt=WHAT'S YOUR CHOICE ? 
echo.
if /i %loginprompt%==u goto updtae
if /i %loginprompt%==Q goto end
goto asklogin

:updtae
echo PREPORATION TO UPDATE...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% <upd1.sql
echo.

echo OPERATION IN PROGRESS...
REM echo ACCOUNTS                                    PROCESSING...
REM %mysqlPath% -h %lshost% -u %lsuser% --password=%lspass% -D %lsdb% < ../sql/accounts.sql
REM echo GAMESERVERS                                 PROCESSING...
REM %mysqlPath% -h %lshost% -u %lsuser% --password=%lspass% -D %lsdb% < ../sql/gameservers.sql
REM echo.
REM echo LOGINSERVER DATABASE UPDATE COMPLETED !
REM echo.
REM echo.
REM pause

CLS
echo please make backup of GS Database
pause
CLS
echo STARTING THE PROCESS...
echo ARMOR                                       PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/armor.sql
echo ARMOR SETS                                  PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/armorsets.sql
echo AUTO CHAT                                   PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/auto_chat.sql
echo AUTO CHAT TEXT                              PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/auto_chat_text.sql
echo BUFF TEMPLATES                              PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/buff_templates.sql
echo CASTLE DOOR                                 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/castle_door.sql
echo CASTLE DOOR UPGRADE                         PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/castle_doorupgrade.sql
echo CASTLE SIEGE GUARDS                         PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/castle_siege_guards.sql
echo CHARACTER TEMPLATES                         PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/char_templates.sql
echo CLASS LIST                                  PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/class_list.sql
echo DROPLIST                                    PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/droplist.sql
echo ENCHANT SKILL TREES                         PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/enchant_skill_trees.sql
echo ETC ITEM                                    PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/etcitem.sql
echo FISH                                        PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/fish.sql
echo FISHING SKILL TREE                          PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/fishing_skill_trees.sql
echo FORTRESS SIEGE                              PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/fortress_siege.sql
echo FOUR SEPULCHURS SPAWNLIST                   PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/four_sepulchers_spawnlist.sql
echo HENNA                                       PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/henna.sql
echo LOCATION                                    PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/locations.sql
echo LVL UP GAIN                                 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/lvlupgain.sql
echo MERCHANT AREAS LIST                         PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/merchant_areas_list.sql
echo MERCHANT BUYLISTS                           PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/merchant_buylists.sql
echo MERCHANT SHOP IDS                           PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/merchant_shopids.sql
echo MERCHANT                                    PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/merchants.sql
echo MINIONS                                     PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/minions.sql
echo NPC                                         PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/npc.sql
echo NPC SKILLS                                  PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/npcskills.sql
echo PET STATS                                   PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/pets_stats.sql
echo PLEDGE SKILL TREES                          PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/pledge_skill_trees.sql
echo RAID BOSS SPAWNLIST                         PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/raidboss_spawnlist.sql
echo RANDOM SPAWN                                PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/random_spawn.sql
echo RANDOM SPAWN LOC                            PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/random_spawn_loc.sql
echo SKILL LEARN                                 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/skill_learn.sql
echo SKILL SPEELBOOKS                            PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/skill_spellbooks.sql
echo SKILL TREE                                  PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/skill_trees.sql
echo SPAWNLIST                                   PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/spawnlist.sql
echo TELEPORT                                    PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/teleport.sql
echo WALKER ROUTES                               PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/walker_routes.sql
echo WEAPON                                      PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/weapon.sql
echo RAID EVENTS SPAWNLIST                       PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/raid_event_spawnlist.sql
echo RAID PRIZES                                 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/raid_prizes.sql
echo JAIL SPAWNLIST                              PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/jail_spawnlist.sql
echo FOUR SEPULCHERS SPAWNLISTS                  PROCESSING...      
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/four_sepulchers_spawnlist.sql
echo VANHALTER SPAWNLIST                         PROCESSING...     
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/vanhalter_spawnlist.sql
echo GRANDBOSS INTERVAL LIST                     PROCESSING...   
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/grandboss_intervallist.sql
echo TvT SETTINGS                                PROCESSING...   
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/tvt.sql
echo BENOM DATA                                  PROCESSING...   
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../sql/benomdata.sql

:end
echo UPDATE COMPLETED!
echo.
echo.
echo.
echo.
echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
echo !                            Developed by NecroLorD                         !
echo !                 COPYRIGHT 2008-2009 - ALL RIGHTS RESERVED.                !
echo !                            Shilen's Temple Dev Team                       !
echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
echo.
echo.
echo.
pause