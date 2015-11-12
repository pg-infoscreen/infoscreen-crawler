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

import java.util.ArrayList;
import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Manager extends Controller
{

	private static ArrayList<APIDescription> api_descriptions = new ArrayList<APIDescription>(10);

	public static class APIDescription
	{

		private final String name;
		private final String url;
		private final String desc;
		private final String example;
		private final ArrayList<APIDescription> subDesc = new ArrayList<Manager.APIDescription>();

		public APIDescription(String url, String name, String desc)
		{
			this(url, name, desc, "");
		}

		public APIDescription(String url, String name, String desc, String example)
		{
			this.url = url;
			this.name = name;
			this.desc = desc;
			this.example = example;
			api_descriptions.add(this);
		}
		
		public APIDescription(String url, String name, String desc, APIDescription api)
		{
			this(url, name, desc, "", api);
		}

		public APIDescription(String url, String name, String desc, String example, APIDescription api)
		{
			this.url = url;
			this.name = name;
			this.desc = desc;
			this.example = example;
			api.getSubDescriptions().add(this);
		}

		public List<APIDescription> getSubDescriptions()
		{
			return subDesc;
		}

		public String getName()
		{
			return name;
		}

		public String getDescription()
		{
			return desc;
		}

		public String getURL()
		{
			return url;
		}

		public String getExample()
		{
			return example;
		}
	}
	
	static{
		// Erzwinge statische Initialisierung		
		RSSController.getAPIDescription();
		XkcdController.getAPIDescription();
	}

	public static Result main()
	{
		List<APIDescription> crawlers = api_descriptions;
		return ok(views.html.main.render(views.html.crawler.render(crawlers)));
	}
	
	private static ObjectNode buildAPIJSON(APIDescription api)
	{
		ObjectNode node = JsonNodeFactory.instance.objectNode();

		node.put("name", api.name);
		node.put("url", api.url);
		if (!api.url.isEmpty())
			node.put("desc", api.desc);
		if (!api.url.isEmpty())
			node.put("example", api.example);

		return node;
	}

	public static Result api()
	{
		/*
		 * Json-Objekt:
		 * 
		 * { url: "$url", name: "$name", desc: "" }
		 */

		ArrayNode resArray = JsonNodeFactory.instance.arrayNode();
		
		for (APIDescription apiDescription : api_descriptions) {
			resArray.add(buildAPIJSON(apiDescription));
		}

		return ok(resArray);

	}

}
