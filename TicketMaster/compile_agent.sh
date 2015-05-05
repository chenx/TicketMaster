#!/bin/bash

# http://jsoup.org/download
# javac -cp <jar you want to include>;<jar you want to include> <source.java> 
# on unix, replace ';' with ':'. If multiple jars, use ',' in between.

javac -classpath .:jsoup-1.8.2.jar Agent.java 
