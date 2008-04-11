@echo off
color 17

echo Addon installer from Plegas. 
echo Dark-Moon project.
echo.
echo db_install.sql on your change.
echo may be trouble.
echo.


pause

REM ############################################
REM ## You can change here your own DB params ##
REM ############################################
REM MYSQL BIN PATH
set mysqlBinPath=E:\Prog`s\xampp\mysql\bin

REM GAMESERVER
set gsuser=root
set gspass=
set gsdb=Dark-Moon
set gshost=localhost
REM ############################################

set mysqldumpPath="%mysqlBinPath%\mysqldump"
set mysqlPath="%mysqlBinPath%\mysql"


echo APDATE ARMOR                               1 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/armor.sql
echo UPDATE CUSTOM_NPC                          2 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/custom_npc.sql
echo UPDATE CUSTOM_SPAWNLIST                    3 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/custom_spawnlist.sql
echo UPDATE DROPLIST                            4 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/droplist.sql
echo UPDATE MERCHANT_BUYLISTS                   5 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/merchant_buylists.sql
echo UPDATE MERCHANT_SHOPIDS                    6 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/merchant_shopids.sql
echo UPDATE NPC                                 7 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/npc.sql
echo UPDATE NPCSKILLS                           8 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/npcskills.sql
echo UPDATE SPAWNLIST                           9 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/spawnlist.sql
echo UPDATE TELEPORT                           10 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/teleport.sql
echo UPDATE WEAPON                             11 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/weapon.sql
echo UPDATE db_install                         12 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/db_install.sql
echo UPDATE clanhall                           13 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/clanhall.sql
echo UPDATE forced_updates                     14 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/forced_updates.sql
echo UPDATE four_sepulchers_spawnlist          15 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/four_sepulchers_spawnlist.sql
echo UPDATE BOSS                               16 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/boss_update.sql
echo UPDATE RANDOM SPAWN LOCATOIN              17 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/random_spawn_loc.sql
echo UPDATE CUSTOM ETCITEM                     18 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/custom_etcitem.sql
echo UPDATE RANDOM SPAWN LIST                  19 PROCESSING...
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < update/random_spawn.sql



:end
echo.
echo.
echo UPDATE GAMESERVER TABLES COMPLETED !
echo Dark-Moon Project Interlude v1.0.
echo.
echo.
echo CONTACTS : plegas@inbox.ru
echo.
echo.
echo POWERED BY Dark-Moon PROJECT TEAM. 
echo COPYRIGHT 2007 - ALL RIGHTS RESERVED.
echo.
echo Patch by Plegas. 
echo.
pause