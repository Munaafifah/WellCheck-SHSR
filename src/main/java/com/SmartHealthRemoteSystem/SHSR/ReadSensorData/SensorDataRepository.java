package com.SmartHealthRemoteSystem.SHSR.ReadSensorData;

import com.SmartHealthRemoteSystem.SHSR.Sensor.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SensorDataRepository {

    private static final String COLLECTION_NAME = "SensorData";

    // Injected MongoDBConnection, managed by Spring
    @Autowired
    private MongoDBConnection mongoDBConnection;

    // ✅ Get sensor data by ID
    public SensorData get(String sensorDataId) {
        MongoDatabase db = mongoDBConnection.connect();
        Document doc = db.getCollection(COLLECTION_NAME)
                .find(new Document("sensorDataId", sensorDataId))
                .first();

        if (doc != null) {
            System.out.println("✅ Sensor data document found: " + doc.toJson());
            return SensorData.fromDocument(doc);
        } else {
            System.out.println("❌ No document found for sensorDataId: " + sensorDataId);
            return null;
        }
    }

    // ✅ Get all sensors (for admin/debug)
    public List<SensorData> getAll() {
        MongoDatabase db = mongoDBConnection.connect();
        List<SensorData> list = new ArrayList<>();
        for (Document doc : db.getCollection(COLLECTION_NAME).find()) {
            list.add(SensorData.fromDocument(doc));
        }
        return list;
    }

    // ✅ Save new sensor data (initial creation)
    public String save(SensorData sensorData) {
        MongoDatabase db = mongoDBConnection.connect();
        sensorData.setTimestamp(Instant.now());
        db.getCollection(COLLECTION_NAME).insertOne(sensorData.toDocument());
        return sensorData.getSensorDataId();
    }

    // ✅ Update real-time sensor data (overwrite latest)
    public String update(SensorData sensorData) {
        MongoDatabase db = mongoDBConnection.connect();
        Document updateDoc = new Document();
        updateDoc.append("heart_Rate", sensorData.getHeart_Rate());
        updateDoc.append("bodyTemperature", sensorData.getBodyTemperature());
        updateDoc.append("ecgReading", sensorData.getEcgReading());
        updateDoc.append("oxygenReading", sensorData.getOxygenReading());
        updateDoc.append("timestamp", Instant.now());

        db.getCollection(COLLECTION_NAME)
          .updateOne(new Document("sensorDataId", sensorData.getSensorDataId()),
                     new Document("$set", updateDoc));

        return "Updated successfully.";
    }

    // ✅ Add nested history reading into history array
    public boolean addToHistory(String sensorDataId, HistorySensorData newEntry) {
        MongoDatabase db = mongoDBConnection.connect();
        Document historyDoc = newEntry.toDocument();
        db.getCollection(COLLECTION_NAME).updateOne(
            Filters.eq("sensorDataId", sensorDataId),
            Updates.push("history", historyDoc)
        );
        return true;
    }

    // ✅ Delete sensor (optional)
    public String delete(String sensorDataId) {
        MongoDatabase db = mongoDBConnection.connect();
        db.getCollection(COLLECTION_NAME)
          .deleteOne(new Document("sensorDataId", sensorDataId));
        return "Deleted sensorDataId: " + sensorDataId;
    }
}
