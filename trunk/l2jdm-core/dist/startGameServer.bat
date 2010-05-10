@echo off
title Game Server Console
:start
echo Starting l2jDarkMoon
echo.

SET OLDCLASSPATH=%CLASSPATH%
call setenv.bat

REM -------------------------------------
REM Default parameters for a basic server.
java -Xmn128m -Xms512m -Xmx1024m -server com.l2jfree.gameserver.GameServer
REM
REM For debug purpose (for devs), use this :
REM java -Xmx512m -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=7456 com.l2jfree.gameserver.GameServer 
REM If you have a big server and lots of memory, you could experiment for example with
REM java -server -Xmx1536m -Xms1024m -Xmn512m -XX:PermSize=256m -XX:SurvivorRatio=8 -Xnoclassgc -XX:+AggressiveOpts
REM -------------------------------------

SET CLASSPATH=%OLDCLASSPATH%

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin Restart ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly
echo.
:end
echo.
echo server terminated
echo.
pause
