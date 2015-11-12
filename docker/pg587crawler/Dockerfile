#############
# Copyright 2015 Fabian Bock, Fabian Bruckner, Christine Dahn, Amin Nirazi, Matthäus Poloczek, Kai Sauerwald, Michael Schultz, Shabnam Tabatabaian, Tim Tegeler und Marvin Wepner
# 
# This file is part of pg-infoscreen.
# 
# pg-infoscreen is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# pg-infoscreen is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with pg-infoscreen.  If not, see <http://www.gnu.org/licenses/>.
# ############

FROM ubuntu:14.04
## Setup
RUN apt-get update
RUN apt-get install -y python-software-properties software-properties-common
RUN add-apt-repository ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get install -y oracle-java8-installer
RUN apt-get install -y wget
RUN apt-get install -y git
RUN apt-get install -y unzip
RUN wget https://dl.bintray.com/sbt/debian/sbt-0.13.7.deb
RUN dpkg -i sbt-0.13.7.deb
RUN rm sbt-0.13.7.deb

## Making directories
RUN mkdir -p /var/akka
WORKDIR /var/akka

# INSTALL SCRAPY
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 627220E7
RUN echo 'deb http://archive.scrapy.org/ubuntu scrapy main' | tee /etc/apt/sources.list.d/scrapy.list
RUN apt-get update && apt-get install -y scrapy

# no cache hack
ADD http://www.random.org/strings/?num=10&len=8&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain&rnd=new uuid

# nodeJS
RUN apt-get install nodejs
RUN apt-get install nodejs-legacy

## CLONING SCHEDULER
RUN git clone ssh://git@github.com:pg-infoscreen/infoscreen-server.git --branch master --single-branch

## Akka
WORKDIR infoscreen-crawler/akka
RUN wget http://downloads.typesafe.com/typesafe-activator/1.2.10/typesafe-activator-1.2.10-minimal.zip
RUN unzip typesafe-activator-1.2.10-minimal.zip
RUN cp activator-1.2.10-minimal/* ./

#COPY startAkka.sh /var/akka/infoscreen/crawler/akka/startAkka.sh

ENTRYPOINT /bin/bash startAkka.sh
