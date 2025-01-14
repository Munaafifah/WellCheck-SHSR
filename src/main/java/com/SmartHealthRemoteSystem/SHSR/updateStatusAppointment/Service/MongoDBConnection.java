package com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDBConnection {
    private static final String CONNECTION_STRING = "mongodb+srv://admin:admin@atlascluster.htlbqbu.mongodb.net/?retryWrites=true&w=majority&appName=AtlasCluster";
    private static final Logger logger = LoggerFactory.getLogger(MongoDBConnection.class);
    private static MongoClient mongoClient = null;

    public static MongoDatabase connect() {
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            return mongoClient.getDatabase("Wellcheck2");
        } catch (Exception e) {
            logger.error("Error establishing MongoDB connection: {}", e.getMessage());
            return null;
        }
    }

    public static void close() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                mongoClient = null;
            } catch (Exception e) {
                logger.error("Error closing MongoDB connection: {}", e.getMessage());
            }
        }
    }
}
