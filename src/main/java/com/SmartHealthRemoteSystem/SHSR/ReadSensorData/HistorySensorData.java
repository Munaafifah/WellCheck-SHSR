package com.SmartHealthRemoteSystem.SHSR.ReadSensorData;

import java.time.Instant;
import java.util.Date;

import org.bson.Document;

public class HistorySensorData {

    private double heart_Rate;
    private double bodyTemperature;
    private double ecgReading;
    private double oxygenReading;
    private Instant timestamp;

    public HistorySensorData() {
        this.timestamp = Instant.now();
    }

    public HistorySensorData(double heart_Rate, double bodyTemperature, double ecgReading, double oxygenReading) {
        this.heart_Rate = heart_Rate;
        this.bodyTemperature = bodyTemperature;
        this.ecgReading = ecgReading;
        this.oxygenReading = oxygenReading;
        this.timestamp = Instant.now();
    }

    // ✅ Getters & Setters
    public double getHeart_Rate() {
        return heart_Rate;
    }

    public void setHeart_Rate(double heart_Rate) {
        this.heart_Rate = heart_Rate;
    }

    public double getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(double bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }

    public double getEcgReading() {
        return ecgReading;
    }

    public void setEcgReading(double ecgReading) {
        this.ecgReading = ecgReading;
    }

    public double getOxygenReading() {
        return oxygenReading;
    }

    public void setOxygenReading(double oxygenReading) {
        this.oxygenReading = oxygenReading;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    // ✅ Convert to MongoDB Document (for nested save)
    public Document toDocument() {
        return new Document("heart_Rate", heart_Rate)
                .append("bodyTemperature", bodyTemperature)
                .append("ecgReading", ecgReading)
                .append("oxygenReading", oxygenReading)
                .append("timestamp", timestamp.toString());
    }

    public static HistorySensorData fromDocument(Document doc) {
    HistorySensorData h = new HistorySensorData();
    h.setHeart_Rate(doc.getDouble("heart_Rate"));
    h.setBodyTemperature(doc.getDouble("bodyTemperature"));
    h.setEcgReading(doc.getDouble("ecgReading"));
    h.setOxygenReading(doc.getDouble("oxygenReading"));

    Object rawTimestamp = doc.get("timestamp");

    if (rawTimestamp instanceof String) {
        h.setTimestamp(Instant.parse((String) rawTimestamp)); // if it's a String
    } else if (rawTimestamp instanceof Date) {
        h.setTimestamp(((Date) rawTimestamp).toInstant()); // if it's a Date
    } else {
        h.setTimestamp(Instant.now()); // fallback just in case
    }

    return h;
}

}
