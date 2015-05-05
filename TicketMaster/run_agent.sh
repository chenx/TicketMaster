#!/bin/bash

# $# is the number of parameters.
#echo $#  

# $@ are all command line parameters this bash script gets.
java -classpath .:jsoup-1.8.2.jar Agent $@

# Code below is deprecated. Can use "$@" above to pass all parameters.
#if [ $# -eq 2 ]
#then
#    #echo param: $1
#    java -classpath .:jsoup-1.8.2.jar Agent $1 $2
#else
#    java -classpath .:jsoup-1.8.2.jar Agent 
#fi
