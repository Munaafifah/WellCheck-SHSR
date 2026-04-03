// package com.SmartHealthRemoteSystem.SHSR.SendDailyHealth;

// // import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorData;
// import com.google.cloud.Timestamp;

// import java.sql.Time;


// public class HealthStatus {
    
//     //add health status Id to prevent conflict in database
//     private String healthStatusId;
//     private String additionalNotes;
//     private String doctorId;
//     private Timestamp timestamp;

//     public HealthStatus() {
//     }

//     public HealthStatus(String additionalNotes, String doctorId) {
//         this.additionalNotes = additionalNotes;
//         this.doctorId = doctorId;
//     }

//     public HealthStatus(String healthStatusId, String additionalNotes, String doctorId, Timestamp  timestamp) {
//         this.healthStatusId = healthStatusId;
//         this.additionalNotes = additionalNotes;
//         this.doctorId = doctorId;
//         this.timestamp = timestamp;
//     }

//     public String getHealthStatusId() {
//         return healthStatusId;
//     }

//     public void setHealthStatusId(String healthStatusId) {
//         this.healthStatusId = healthStatusId;
//     }

//     public String getAdditionalNotes() {
//         return additionalNotes;
//     }

//     public void setAdditionalNotes(String additionalNotes) {
//         this.additionalNotes = additionalNotes;
//     }

//     public String getDoctorId() {
//         return doctorId;
//     }

//     public void setDoctorId(String doctorId) {
//         this.doctorId = doctorId;
//     }

//     public Timestamp getTimestamp() {
//         return timestamp;
//     }

//     public void setTimestamp(Timestamp timestamp) {
//         this.timestamp = timestamp;
//     }
// }

//MongoDB//

package com.SmartHealthRemoteSystem.SHSR.SendDailyHealth;

import org.springframework.data.annotation.Id;
import java.time.Instant;
import java.util.Date;

public class HealthStatus {

    @Id
    private String healthStatusId;
    private String additionalNotes;
    private String doctorId;
    private Instant timestamp;

    public HealthStatus() {}

    public HealthStatus(String additionalNotes, String doctorId) {
        this.additionalNotes = additionalNotes;
        this.doctorId = doctorId;
        this.timestamp = Instant.now();
    }

    // Getters and Setters

    public String getHealthStatusId() {
        return healthStatusId;
    }

    public void setHealthStatusId(String healthStatusId) {
        this.healthStatusId = healthStatusId;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

      public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}

