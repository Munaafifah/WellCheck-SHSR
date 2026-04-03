package com.SmartHealthRemoteSystem.SHSR.Sensor;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MongoDBConnection {
    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Value("${spring.data.mongodb.database}")
    private String dbName;

    private MongoClient mongoClient = null;

    public MongoDatabase connect() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(connectionString);
        }
        return mongoClient.getDatabase(dbName);
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}
