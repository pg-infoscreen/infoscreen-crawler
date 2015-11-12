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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import javax.imageio.ImageIO;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import sample.hello.scheduling.Schedulable;
import sample.hello.scheduling.SchedulerMessage;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class XkcdCrawler extends UntypedActor implements Schedulable {

    private final class Image {
        private final byte[] data;
        private final int width;
        private final int height;

        public Image(byte[] data, int width, int height) {
            this.data = data;
            this.width = width;
            this.height = height;
        }

    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg == CrawlerMessage.CRAWL) {
            crawl();
            getSender().tell(SchedulerMessage.ready(this), getSelf());
        } else {
            unhandled(msg);
        }
    }

    private long getCurrentId() throws IOException {
        URL url = new URL("http://xkcd.com/info.0.json");
        URLConnection connection = url.openConnection();
        try (InputStream is = connection.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode object = (ObjectNode) mapper.readTree(is);
            long id = object.get("num").asLong();
            return id;
        }
    }

    private void get(long id) throws IOException {
        URL url = new URL(String.format("http://xkcd.com/%d/info.0.json", id));
        URLConnection connection = url.openConnection();
        try (InputStream is = connection.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode object = (ObjectNode) mapper.readTree(is);
            long num = object.get("num").asLong();
            String title = object.get("title").asText();
            String alt = object.get("alt").asText();
            String img = object.get("img").asText();
            MongoClient client = new DatabaseManager().getMongoClient();
            DBCollection comics = client.getDB("infoscreen").getCollection("xkcd");
            Image image = getImage(id, img);
            DBObject imageDoc = new BasicDBObject().append("data", image.data)
                    .append("width", image.width)
                    .append("height", image.height);
            DBObject comic = new BasicDBObject().append("id", num)
                    .append("title", title).append("alt", alt)
                    .append("image", imageDoc);
            comics.insert(comic, WriteConcern.JOURNALED);
            System.out.println(String.format(
                    "Downloaded XKCD comic number %d.", id));
        }
    }

    private Image getImage(long id, String img) throws IOException {
        URL url = new URL(img);
        BufferedImage image = ImageIO.read(url);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int width = image.getWidth();
        int height = image.getHeight();
        ImageIO.write(image, "PNG", out);
        return new Image(out.toByteArray(), width, height);
    }

    private long getNewestId() throws UnknownHostException {
        DB db = new DatabaseManager().getMongoClient().getDB("infoscreen");
        DBCollection collection = db.getCollection("xkcd");
        DBObject keys = new BasicDBObject("id", Long.valueOf(-1));
        DBObject comic = collection.find(new BasicDBObject(), keys).sort(keys)
                .one();
        if (comic != null) {
            long id = Long.valueOf((Long) comic.get("id"));
            return id;
        } else {
            return 0;
        }
    }

    private void crawl() {
        ImageIO.setUseCache(false);
        try {
            long newestId = getNewestId();
            long currentId = getCurrentId();
            if (newestId < currentId) {
                for (long id = newestId + 1; id <= currentId; ++id) {
                    if (id != 404) {
                        get(id);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ReadableInstant getNextInstant() {
        // Crawl every day (change to correct days of week later)
        return new Instant().plus(Duration.standardDays(1));
    }

    @Override
    public ActorRef getReference() {
        return self();
    }

}