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
#RUN apt-get install -y openjdk-7-jdk
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get install -y oracle-java8-installer
RUN apt-get install -y wget
RUN apt-get install -y git
RUN apt-get install -y unzip

## Install sbt
RUN wget https://dl.bintray.com/sbt/debian/sbt-0.13.7.deb
RUN dpkg -i sbt-0.13.7.deb
RUN rm sbt-0.13.7.deb

## Python and stuff
RUN apt-get install -y  build-essential python3-dev python3-setuptools python3-numpy python3-scipy python3-pip
RUN pip3 install scikit-learn
RUN pip3 install nltk
RUN pip3 install pymongo
RUN python3 -m nltk.downloader -d /usr/share/nltk_data all


## Making directories
WORKDIR /var

# no cache hack
ADD http://www.random.org/strings/?num=10&len=8&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain&rnd=new uuid

## CLONING SCHEDULER
RUN echo "Clear cache"
RUN git clone ssh://git@github.com:pg-infoscreen/infoscreen-crawler.git --branch master --single-branch
WORKDIR infoscreen-crawler/play/

#COPY startPlay.sh /var/infoscreen/crawler/play/startPlay.sh

#EXPOSE 9000 not necessary

## RUN
ENTRYPOINT /bin/bash startPlay.sh
