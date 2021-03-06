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
    docker stop pg587crawler
    sleep 1 && sync  # Give the system time to freeing ressources
    docker rm pg587crawler
    docker rmi pg587crawler
    cd pg587crawler/
    docker build -t pg587crawler .
    docker run -d --name pg587crawler --link mongoDB:mongoDB -t pg587crawler
}

function Deployment(){
    docker stop pg587crawler
    docker rm pg587crawler
    docker run -d --name pg587crawler --link mongoDB:mongoDB -t pg587crawler
}

function dockerRestart(){
    docker stop pg587crawler
    sleep 5 && sync  # Give the system time to freeing ressources
    docker start pg587crawler
}

function dockerAttach(){
    docker attach pg587crawler
}

function dockerExecBash(){
    docker exec -it pg587crawler bash
}

function showHELP(){
    echo "ACHTUNG: Eventuell müssen pg587crawler und Docker danach neu gestartet werden."
    echo "Befehle:"
    echo "nix      Startet den mongoDB Container neu."
    echo "--depl   Löscht den Container, und erstellt Ihn neu"
    echo "--full   Löscht die Images und erstellt & starten den Container komplett neu."
    echo "--attach  Hängt die STDOUT an den Container an"
    echo "--bash   Öffnet eine BASH in dem Container"
    echo "--help   Zeigt diese Hilfe"
}

if [ 1 -eq $# -a \( "$1" == "--full" -o "$1" == "full" \) ]
    then fullDeployment; exit $?
elif [ 1 -eq $# -a \( "$1" == "--depl" -o "$1" == "depl" \) ]
    then Deployment; exit $?
elif [ 1 -eq $# -a \( "$1" == "--attach" -o "$1" == "attach" \) ]
    then dockerAttach; exit $?
elif [ 1 -eq $# -a \( "$1" == "--bash" -o "$1" == "bash" \) ]
    then dockerExecBash; exit $?
elif [ 1 -eq $# -a \( "$1" == "--help" -o "$1" == "help" \) ]
    then showHELP; exit $?
else
    dockerRestart; exit $?
fi
