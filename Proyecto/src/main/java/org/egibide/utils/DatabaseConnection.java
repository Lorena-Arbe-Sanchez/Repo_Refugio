package org.egibide.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private DatabaseConnection() {
        String uri = "mongodb://localhost:27017";
        String databaseName = "bd_refugio";

        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase(databaseName);
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            instance = null;
        }
    }
}