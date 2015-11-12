#! /usr/bin/bash

git pull
export _JAVA_OPTIONS="-Xms2048m -Xmx4096m -XX:PermSize=2048m"
sbt clean start -Xms2048M -Xmx4096m
