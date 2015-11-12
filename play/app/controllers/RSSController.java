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

import play.mvc.Result;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import controllers.Manager.APIDescription;

public class RSSController extends Application
{
	private static APIDescription desc;
	public static APIDescription getAPIDescription()
	{
		return desc;
	}

	static {
		String url = "/rss";
		String name = "RSS Feeds";
		String text = "Stellt eine Übersicht über di gecrawlten RSS-Feeds zur verfügung";
		desc = new APIDescription(url, name, text);

		url = "/rss/heise";
		name = "RSS Feed - Heise";
		text = "Heise-News-Feed";
		new APIDescription(url, name, text, desc);

		url = "/rss/golem";
		name = "RSS Feed - Golem";
		text = "Golem-News-Feed";
		new APIDescription(url, name, text, desc);
		
		url = "/rss/gi";
		name = "RSS Feed - Gesellschaft für Informatik";
		text = "Feed der Gesellschaft für Informatik";
		new APIDescription(url, name, text, desc);

		url = "/rss/lwn";
		name = "RSS Feed - Linux Weekly News";
		text = "Feed der Linux Weekly News";
		new APIDescription(url, name, text, desc);

		url = "/rss/pg-infoscreen";
		name = "PG Feed";
		text = "Feed vom Redmine des Projekt PG-Infoscreen";
		new APIDescription(url, name, text, desc);
		
		url = "/rss/ard";
		name = "ARD Tagesschau Feed";
		text = "Feed der Tagesschau";
		new APIDescription(url, name, text, desc);
	}

	public static Result rss(String res)
	{
		DB db = getDbClient().getDB("infoscreen");
		DBCollection rss = db.getCollection("rss");
		DBObject current = rss.find(new BasicDBObject("Kategorie", res)).sort(new BasicDBObject("_id", Long.valueOf(-1))).one();

		if (current != null)
			return ok(current.toString());

		return notFound();
	}

}