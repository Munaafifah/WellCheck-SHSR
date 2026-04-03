package com.SmartHealthRemoteSystem.SHSR.Sensor.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.bson.Document;

import java.util.UUID;

@Service
public class UniqueKeyGenerator {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String COLLECTION_NAME = "Sensor";

    /**
     * Generate a new unique key and store into Sensor collection.
     */
    public String generateAndStore() {
        String uniqueKey = UUID.randomUUID().toString();

        // Check key existence
        // ✅ consistent field name
        Query query = new Query(Criteria.where("uniqueKey").is(uniqueKey));
        boolean exists = mongoTemplate.exists(query, COLLECTION_NAME);


        if (exists) {
            // Recursive regenerate if collision occurs (very rare for UUID)
            return generateAndStore();
        }

        Document keyDoc = new Document("uniqueKey", uniqueKey)
        .append("sensorDataId", null);


        mongoTemplate.insert(keyDoc, COLLECTION_NAME);
        return uniqueKey;
    }
}
