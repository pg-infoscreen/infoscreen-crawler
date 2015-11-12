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

import play.mvc.Controller;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public abstract class Application extends Controller {
    
    // CACHE!!!!!!!
    private static MongoClientNoDeprec cachedClient;
    
    public static MongoClientNoDeprec getDbClient() {
        
        if (cachedClient == null)
        {
            String host = System.getenv("MONGODB_PORT_27017_TCP_ADDR") == null ?
                    "localhost" : System.getenv("MONGODB_PORT_27017_TCP_ADDR");
            String portString = System.getenv("MONGODB_PORT_27017_TCP_PORT");
            int port = portString == null ? 27017 : Integer.parseInt(portString);
            cachedClient = new MongoClientNoDeprec(host, port);
        }
        
        return cachedClient;
    }
    
    // nur um die deprec fehlermeldung wegzubekommen, suppress zentral
    public static class MongoClientNoDeprec extends MongoClient {
        
        public MongoClientNoDeprec(String host, int port) {
            super(host, port);
        }
        
        @SuppressWarnings("deprecation")
        @Override
        public DB getDB( String dbname ){
            return super.getDB(dbname);
        }
    }

}