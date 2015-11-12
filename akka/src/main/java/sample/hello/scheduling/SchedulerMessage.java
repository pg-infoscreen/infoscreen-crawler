/**
Copyright 2015 Fabian Bock, Fabian Bruckner, Christine Dahn, Amin Nirazi, Matthäus Poloczek, Kai Sauerwald, Michael Schultz, Shabnam Tabatabaian, Tim Tegeler und Marvin Wepner

This file is part of pg-infoscreen.

pg-infoscreen is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

pg-infoscreen is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with pg-infoscreen.  If not, see <http://www.gnu.org/licenses/>.
*/
package sample.hello.scheduling;

public enum SchedulerMessage {

    SCHEDULE;

    public static class Ready {

        private final Schedulable schedulable;

        public Ready(Schedulable schedulable) {
            this.schedulable = schedulable;
        }

        public Schedulable getSchedulable() {
            return schedulable;
        }

    }

    public static Ready ready(Schedulable schedulable) {
        return new Ready(schedulable);
    }

}