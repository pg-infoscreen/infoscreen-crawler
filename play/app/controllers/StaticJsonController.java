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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import play.mvc.Controller;
import play.mvc.Result;
import controllers.Manager.APIDescription;

public abstract class StaticJsonController extends Controller {

	private static APIDescription desc;
	public static APIDescription getAPIDescription()
	{
		return desc;
	}
	
	static {
		String url = "/static/:name";
		String name = "Statische JSON-Objekte";
		String text = "Statische Files Können auf anfrage hinterlegt werden, Diese sind dann unter :name erreichbar.";
		desc = new APIDescription(url, name, text);
}
	
    private static final File DIR = new File("../static content");

    public static Result get(String file) {
        File content = new File(DIR, file);
        try (Reader fileReader = new FileReader(content); BufferedReader reader = new BufferedReader(fileReader)) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return ok(result.toString());
        } catch (IOException e) {
            return notFound();
        }
    }

}