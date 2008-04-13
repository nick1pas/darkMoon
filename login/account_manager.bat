@echo off
color 17
title Account Manager
SET OLDCLASSPATH=%CLASSPATH%
call setenv.bat

REM ########################################################################
REM # You need to set here your JDK/JRE params in case of x64 bits System. #
REM # Remove the "REM" after set PATH variable                             #
REM # If you're not a x64 system user just leave                           # 
REM ########################################################################
REM set PATH="type here your path to java jdk/jre (including bin folder)"

@java -Djava.util.logging.config.file=console.cfg net.sf.l2j.accountmanager.AccountManager

SET CLASSPATH=%OLDCLASSPATH%
@pause
