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
package sample.hello;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import sample.hello.scheduling.CrawlerScheduler;
import sample.hello.scheduling.SchedulerMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Main {

    public static void main(String[] args) {
        Logger mongoLogger = LogManager.getLogManager().getLogger("");
        if (mongoLogger != null) {
            mongoLogger.setLevel(Level.WARNING);
        }
        ActorSystem system = ActorSystem.create("Hello");
        ActorRef a = system.actorOf(Props.create(CrawlerScheduler.class), "scheduler");
        system.actorOf(Props.create(Terminator.class, a), "terminator");

        a.tell(SchedulerMessage.SCHEDULE, null);
    }

    public static class Terminator extends UntypedActor {

        private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
        private final ActorRef ref;

        public Terminator(ActorRef ref) {
            this.ref = ref;
            getContext().watch(ref);
        }

        @Override
        public void onReceive(Object msg) {
            if (msg instanceof Terminated) {
                log.info("{} has terminated, shutting down system", ref.path());
                getContext().system().shutdown();
            } else {
                unhandled(msg);
            }
        }

    }

}