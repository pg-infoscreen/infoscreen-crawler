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

import sample.hello.db.DatabaseManager;

import java.net.URL;
import java.util.GregorianCalendar;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import sample.hello.scheduling.Schedulable;
import sample.hello.scheduling.SchedulerMessage;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedHeader;
import it.sauronsoftware.feed4j.bean.FeedItem;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

//"userGuid": "ada291eb-9cce-48d6-a812-3cbffbb9eb50",
//"apiKey": "hG20dB35FYwORTyK95sXdpbVR3dsDm38xm5nvimAsnJftjUDTEiyZ9oCpwuNc8+jTUhJiKBJ0q0LazPDpr3rUw=="

public class RSSCrawler extends UntypedActor implements Schedulable {

    private final String[] urls = {
            "http://www.gi-ev.de/aktuelles/rss.xml",
            "https://projekte.itmc.tu-dortmund.de/projects/pg-infoscreen/activity.atom?key=c9999bfaa64e6fedf14fa41953ad422bba506684",
            "http://www.heise.de/newsticker/heise-atom.xml",
            "http://rss.golem.de/rss.php?feed=ATOM1.0",
            "http://lwn.net/headlines/rss", "http://www.tagesschau.de/xml/rss2" };

    private final String[] dbKeys = { "gi", "pg-infoscreen", "heise", "golem",
            "lwn", "ard" };

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void onReceive(Object msg) {
        if (msg == CrawlerMessage.CRAWL) {
            for (int i = 0; i < urls.length; ++i) {
                crawl(i);
            }
            getSender().tell(SchedulerMessage.ready(this), getSelf());
        } else {
            unhandled(msg);
        }
    }

    private void crawl(int i) {
        MongoClient client = new DatabaseManager().getMongoClient();
        try {
            System.out.println("[RSS-Crawler] Starte RSS-Crawler [" + dbKeys[i]
                    + "]: " + urls[i]);
            // Get current date.
            GregorianCalendar calendar = new GregorianCalendar();

            BasicDBObject main = new BasicDBObject();
            main.append("date", calendar.getTimeInMillis());
            main.append("Kategorie", dbKeys[i]);
            main.append("RSS-URL", urls[i]);

            // init feed
            URL url = new URL(urls[i]);
            Feed feed = FeedParser.parse(url);

            FeedHeader header = feed.getHeader();

            if (header.getTitle() != null)
                main.append("Title", header.getTitle());
            if (header.getLink() != null)
                main.append("Link", header.getLink().toExternalForm());
            if (header.getDescription() != null)
                main.append("Description", header.getDescription());

            BasicDBList list = new BasicDBList();

            int items = feed.getItemCount();
            for (int j = 0; j < items; j++) {
                FeedItem item = feed.getItem(j);

                BasicDBObject dbobj = new BasicDBObject();

                if (item.getTitle() != null)
                    dbobj.append("Title", item.getTitle());
                if (item.getLink() != null)
                    dbobj.append("link", item.getLink().toExternalForm());
                if (item.getDescriptionAsText() != null)
                    dbobj.append("text", item.getDescriptionAsText());
                if (item.getDescriptionAsHTML() != null)
                    dbobj.append("html", item.getDescriptionAsHTML());
                if (item.getElementValue("", "pubDate") != null)
                    dbobj.append("date", item.getElementValue("", "pubDate"));

                list.add(dbobj);
            }

            main.append("items", list);

            // get address of db
            DB db = client.getDB("infoscreen");
            DBCollection rss = db.getCollection("rss");

            rss.insert(main);

        } catch (Exception e) {
            System.out.println(e);
            System.out.println(e.getStackTrace());
        }

        System.out.println("[RSS-Crawler] Stoppe RSS-Crawler [" + dbKeys[i]
                + "]");

    }

    @Override
    public ReadableInstant getNextInstant() {
        return new Instant().plus(Duration.standardHours(1));
    }

    @Override
    public ActorRef getReference() {
        return self();
    }

}