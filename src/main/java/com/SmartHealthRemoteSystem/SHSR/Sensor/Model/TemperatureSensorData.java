// package com.SmartHealthRemoteSystem.SHSR.Sensor.Model;

// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;
// import java.time.Instant;

// @Document(collection = "TemperatureSensorData")
// public class TemperatureSensorData {

//     @Id
//     private String id;

//     private double ambient;
//     private double objectTemperature;
//     private Instant timestamp;

//     public TemperatureSensorData() {
//         this.timestamp = Instant.now();
//     }

//     public TemperatureSensorData(double ambient, double objectTemperature) {
//         this.ambient = ambient;
//         this.objectTemperature = objectTemperature;
//         this.timestamp = Instant.now();
//     }

//     // Getters and setters
//     public String getId() {
//         return id;
//     }

//     public double getAmbient() {
//         return ambient;
//     }

//     public void setAmbient(double ambient) {
//         this.ambient = ambient;
//     }

//     public double getObjectTemperature() {
//         return objectTemperature;
//     }

//     public void setObjectTemperature(double objectTemperature) {
//         this.objectTemperature = objectTemperature;
//     }

//     public Instant getTimestamp() {
//         return timestamp;
//     }

//     public void setTimestamp(Instant timestamp) {
//         this.timestamp = timestamp;
//     }
// }
