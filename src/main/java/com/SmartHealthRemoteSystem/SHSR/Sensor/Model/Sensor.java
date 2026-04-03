
//MongoDB//

package com.SmartHealthRemoteSystem.SHSR.Sensor.Model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.HistorySensorData;

import java.time.Instant;
import java.util.List;

@Document(collection = "Sensor")
public class Sensor {

    private String sensorDataId;
   // In Sensor.java
    @Field("uniqueKey")
    private String uniqueKey;


    private double heart_Rate;
    private double bodyTemperature;
    private double ecgReading;
    private double oxygenReading;
    private Instant timestamp;

    public Sensor() {
        this.timestamp = Instant.now();
    }

    public Sensor(String sensorDataId, String uniqueKey, double heart_Rate,
                  double bodyTemperature, double ecgReading, double oxygenReading) {
        this.sensorDataId = sensorDataId;
        this.uniqueKey = uniqueKey;
        this.heart_Rate = heart_Rate;
        this.bodyTemperature = bodyTemperature;
        this.ecgReading = ecgReading;
        this.oxygenReading = oxygenReading;
        this.timestamp = Instant.now();
    }

    // Getters and setters
    public String getSensorDataId() {
        return sensorDataId;
    }

    public void setSensorDataId(String sensorDataId) {
        this.sensorDataId = sensorDataId;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

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

    public List<HistorySensorData> getHistory() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHistory'");
    }
}
