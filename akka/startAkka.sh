#! /usr/bin/bash

git pull
export _JAVA_OPTIONS="-Xms1024m -Xmx2048m -XX:PermSize=1024m"
sbt clean run -Xms2048M -Xmx4096m
