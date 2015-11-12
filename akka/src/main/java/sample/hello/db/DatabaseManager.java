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
package sample.hello.db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public final class DatabaseManager {

    private static enum MongoClientManager {

        INSTANCE;

        private final MongoClient MONGO_CLIENT;

        private MongoClientManager() {
            final String hostEnv = System.getenv("MONGODB_PORT_27017_TCP_ADDR");
            final String host = hostEnv == null ? "localhost" : hostEnv;
            final String portEnv = System.getenv("MONGODB_PORT_27017_TCP_PORT");
            final int port = portEnv == null ? 27017 : Integer.valueOf(portEnv);
            MONGO_CLIENT = new MongoClient(host, port);
        }
    }

    public DatabaseManager() {
        
    }

    public MongoClient getMongoClient() {
        return MongoClientManager.INSTANCE.MONGO_CLIENT;
    }

    public MongoDatabase getDatabase() {
        return getMongoClient().getDatabase("infoscreen");
    }

    public MongoDatabase getDatabase(String databaseName) {
        return getMongoClient().getDatabase(databaseName);
    }

}