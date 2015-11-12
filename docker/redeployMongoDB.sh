#! /bin/bash
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

function fullDeployment(){
    docker pull mongo:latest
    docker stop pg587mongo
    docker rm pg587mongo
    docker run -d --name pg587mongo -v /data/d01/mongo:/data/db -t mongo --smallfiles
}

function Deployment(){
    docker stop pg587mongo
    docker rm pg587mongo
    #docker run -d -v $(pwd)/mongoBackup:/home --name pg587mongo -t mongo --smallfiles
    docker run -d --name pg587mongo -v /data/d01/mongo:/data/db -t mongo --smallfiles
}

function dockerRestart(){
    docker stop pg587mongo
    sleep 5 && sync;
    docker start pg587mongo
}

function dockerBackup(){
    docker exec -it pg587mongo chmod u+x /home/makeBackup.sh
    docker exec -it pg587mongo bash /home/makeBackup.sh
}

function dockerAttach(){
    docker attach pg587mongo
}

function dockerExecBash(){
    docker exec -it pg587mongo bash
}

function showHELP(){
    echo "ACHTUNG: Eventuell müssen Akka und Docker danach neu gestartet werden."
    echo "Befehle:"
    echo "nix      Startet den pg587mongo Container neu."
    echo "--depl   Löscht den Container, und erstellt Ihn neu"
    echo "--full   Löscht die Images und erstellt & starten den Container komplett neu."
    echo "--backup Dumpt den Inhalt der DB in den Backupfolder"
    echo "--attach Hängt die STDOUT an den Container an"
    echo "--bash   Öffnet eine BASH in dem Container"
    echo "--help   Zeigt diese Hilfe"
}

if [ 1 -eq $# -a \( "$1" == "--full" -o "$1" == "full" \) ]
    then fullDeployment; exit $?
elif [ 1 -eq $# -a \( "$1" == "--depl" -o "$1" == "depl" \) ]
    then Deployment; exit $?
elif [ 1 -eq $# -a \( "$1" == "--backup" -o "$1" == "backup" \) ]
    then dockerBackup; exit $?
elif [ 1 -eq $# -a \( "$1" == "--attach" -o "$1" == "attach" \) ]
    then dockerAttach; exit $?
elif [ 1 -eq $# -a \( "$1" == "--bash" -o "$1" == "bash" \) ]
    then dockerExecBash; exit $?
elif [ 1 -eq $# -a \( "$1" == "--help" -o "$1" == "help" \) ]
    then showHELP; exit $?
else
    dockerRestart; exit $?
fi
