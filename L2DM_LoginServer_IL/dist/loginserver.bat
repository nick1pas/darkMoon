@echo off
color 17
title L2EmuProject:  Login Server Console
:start
echo Initializing L2EmuProject LoginServer.
echo.

SET OLDCLASSPATH=%CLASSPATH%
call setenv.bat

REM ########################################################################
REM # You need to set here your JDK/JRE params in case of x64 bits System. #
REM # Remove the "REM" after set PATH variable                             #
REM # If you're not a x64 system user just leave                           # 
REM ########################################################################
REM set PATH="type here your path to java jdk/jre (including bin folder)"

java -Dfile.encoding=UTF-8 -Xmx64m net.sf.l2j.loginserver.L2LoginServer

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
echo LoginServer terminated abnormaly
echo.
:end
echo.
echo LoginServer terminated
echo.
pause
