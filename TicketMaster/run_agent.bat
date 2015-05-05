@echo off

REM
REM There should be 2 or 4 parameters. If not, go to usage.
REM

SET /A ARGS_COUNT=0    
FOR %%A in (%*) DO SET /A ARGS_COUNT+=1    
REM ECHO %ARGS_COUNT%

if "%ARGS_COUNT%"=="2" GOTO exec1
if "%ARGS_COUNT%"=="4" GOTO exec2 
GOTO :usage

:exec1
java -classpath .;jsoup-1.8.2.jar Agent %1 %2
GOTO end

:exec2
java -classpath .;jsoup-1.8.2.jar Agent %1 %2 %3 %4
GOTO end

:usage
java -classpath .;jsoup-1.8.2.jar Agent

:end
