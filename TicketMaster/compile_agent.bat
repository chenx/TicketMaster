@echo off
REM http://jsoup.org/download
REM javac -cp <jar you want to include>;<jar you want to include> <source.java> 
REM on unix, replace ';' with ':'. If multiple jars, use ',' in between.
@echo on

javac -classpath .;jsoup-1.8.2.jar Agent.java 
