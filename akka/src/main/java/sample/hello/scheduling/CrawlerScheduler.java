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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.ReadableInstant;
import org.joda.time.Instant;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Scheduler;
import akka.actor.UntypedActor;
import sample.hello.RSSCrawler;
import sample.hello.XkcdCrawler;
import sample.hello.CrawlerMessage;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class CrawlerScheduler extends UntypedActor {

    private final List<ActorRef> crawlers = Collections.unmodifiableList(
            Arrays.asList(getContext().actorOf(Props.create(RSSCrawler.class), "rss-crawler"),
                    getContext().actorOf(Props.create(XkcdCrawler.class), "xkcd-crawler")
));

    
    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg == SchedulerMessage.SCHEDULE) {
            for (ActorRef crawler : crawlers) {
                crawler.tell(CrawlerMessage.CRAWL, self());
            }
        } else if (msg instanceof SchedulerMessage.Ready) {
            SchedulerMessage.Ready ready = (SchedulerMessage.Ready) msg;
            schedule(ready.getSchedulable());
        } else {
            unhandled(msg);
        }
    }

    private void schedule(Schedulable schedulable) {
        Scheduler scheduler = context().system().scheduler();
        ExecutionContext context = context().dispatcher();
        ActorRef receiver = schedulable.getReference();
        org.joda.time.Duration duration = new org.joda.time.Duration(new Instant(), schedulable.getNextInstant());
        long ms = duration.getMillis();
        System.out.println(receiver + " will be run in " + ms + " ms");
        FiniteDuration delay = Duration.create(ms, TimeUnit.MILLISECONDS);
        Object message = CrawlerMessage.CRAWL;
        scheduler.scheduleOnce(delay, receiver, message, context, self());
    }
}