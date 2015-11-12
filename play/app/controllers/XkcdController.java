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
package controllers;

import java.util.Random;

import play.mvc.Result;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.BaseEncoding;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import controllers.Manager.APIDescription;

public class XkcdController extends Application {
    private static APIDescription desc;

    public static APIDescription getAPIDescription() {
        return desc;
    }

    static {
        String url = "/xkcd";
        String name = "XKCD Comics";
        String text = "Stellt die Liste aller XKCD Comics bereit. Achtung ~ 200 MB.";
        desc = new APIDescription(url, name, text);

        url = "/xkcd/random";
        name = "XKCD Comics - Random";
        text = "Stellt einen zufälligen XKCD Comic bereit.";
        new APIDescription(url, name, text, desc);

        url = "/xkcd/:id";
        name = "XKCD Comics - ID";
        text = "Über den Parameter :id (int) wird der XKCD Comic der entsprechenden Nummer geliefert.";
        new APIDescription(url, name, text, desc);
    }

    public static Result getAll() {
        DB db = getDbClient().getDB("infoscreen");
        DBCollection xkcd = db.getCollection("xkcd");
        DBCursor cursor = xkcd.find();
        Chunks<String> chunks = new AllWriter(cursor);
        return ok(chunks);
    }

    private static class AllWriter extends StringChunks {

        private final DBCursor cursor;

        private AllWriter(DBCursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public void onReady(Chunks.Out<String> out) {
            out.write("[");
            while (cursor.hasNext()) {
                DBObject comic = cursor.next();
                out.write(decode(comic).toString());
                if (cursor.hasNext()) {
                    out.write(", ");
                }
            }
            out.write("]");
        }
    }

    public static Result get(Long id) {
        DB db = getDbClient().getDB("infoscreen");
        DBCollection xkcd = db.getCollection("xkcd");
        DBObject comic = xkcd.find(new BasicDBObject("id", id)).one();
        return ok(decode(comic));
    }

    public static Result random() {
        DBCursor cursor = getCursor();
        int random = new Random().nextInt(cursor.count());
        DBObject comic = cursor.skip(random).next();
        return ok(decode(comic));
    }

    private static ObjectNode decode(DBObject object) {
        long id = ((Long) object.get("id")).longValue();
        String title = (String) object.get("title");
        String alt = (String) object.get("alt");
        BasicDBObject imageDoc = (BasicDBObject) object.get("image");
        byte[] img = (byte[]) imageDoc.get("data");
        int width = ((Integer) imageDoc.get("width")).intValue();
        int height = ((Integer) imageDoc.get("height")).intValue();
        String encodedImg = BaseEncoding.base64().encode(img);
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        result.put("id", id);
        result.put("title", title);
        result.put("alt", alt);
        ObjectNode imageNode = result.putObject("image");
        imageNode.put("data", encodedImg);
        imageNode.put("width", width);
        imageNode.put("height", height);
        return result;
    }

    private static DBCursor getCursor() {
        DB db = getDbClient().getDB("infoscreen");
        DBObject ref = new BasicDBObject().
                append("image.width", new BasicDBObject("$lte", 640)).
                append("image.height", new BasicDBObject("$lte", 480));
        DBObject keys = new BasicDBObject();
        DBCollection xkcd = db.getCollection("xkcd");
        return xkcd.find(ref, keys);
    }
}