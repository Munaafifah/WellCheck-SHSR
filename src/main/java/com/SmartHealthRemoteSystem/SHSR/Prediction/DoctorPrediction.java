// package com.SmartHealthRemoteSystem.SHSR.Prediction;

// import java.util.List;

// public class DoctorPrediction {
//     public String predictionID;
//   private List<String> symptomsList;
//   private List<String> diagnosisList;
//   private List<Float> probabilityList;
//   private String timestamp;

//   public DoctorPrediction(){
//   }

//   public DoctorPrediction(String predictionID, List<String> symptomsList, List<String> diagnosisList, List<Float> probabilityList, String timestamp){
//     this.predictionID = predictionID;
//     this.symptomsList= symptomsList;
//     this.diagnosisList = diagnosisList;
//     this.probabilityList = probabilityList;
//     this.timestamp= timestamp;
//   }

//   public String getPredictionID(){
//     return predictionID;
//   }

//   public void setPredictionID(String predictionID){
//     this.predictionID= predictionID;
//   }

//   public String getTimestamp(){
//     return timestamp;
//   }

//   public void setTimestamp(String timestamp){
//     this.timestamp=timestamp;
//   }

//   public List<String> getSymptomsList(){
//     return symptomsList;
//   }

//   public void setSymptomsList(List<String> symptomsList){
//     this.symptomsList=symptomsList;
//   }

//   public List<String> getDiagnosisList(){
//     return diagnosisList;
//   }

//   public void setDiagnosisList(List<String> diagnosisList){
//     this.diagnosisList=diagnosisList;
//   }

//   public List<Float> getProbabilityList(){
//     return probabilityList;
//   }

//   public void setProbabilityList(List<Float> probabilityList){
//     this.probabilityList = probabilityList;
//   }

// }


//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.Prediction;

import java.time.Instant;
import java.util.List;

/**
 * Represents a diagnosis made by a doctor using machine learning predictions.
 */
public class DoctorPrediction {
   private String predictionId;
    private List<String> symptomsList;
    private List<String> diagnosisList;
    private List<Float> probabilityList;
    private Instant timestamp;

    // Default constructor
    public DoctorPrediction() {}

    // All-args constructor
    public DoctorPrediction(String predictionID, List<String> symptomsList, List<String> diagnosisList,
                            List<Float> probabilityList, Instant timestamp) {
        this.predictionId = predictionID;
        this.symptomsList = symptomsList;
        this.diagnosisList = diagnosisList;
        this.probabilityList = probabilityList;
        this.timestamp = timestamp;
    }

    public String getPredictionId() {
    return predictionId;
}

public void setPredictionId(String predictionId) {
    this.predictionId = predictionId;
}

    public List<String> getSymptomsList() {
        return symptomsList;
    }

    public void setSymptomsList(List<String> symptomsList) {
        this.symptomsList = symptomsList;
    }

    public List<String> getDiagnosisList() {
        return diagnosisList;
    }

    public void setDiagnosisList(List<String> diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public List<Float> getProbabilityList() {
        return probabilityList;
    }

    public void setProbabilityList(List<Float> probabilityList) {
        this.probabilityList = probabilityList;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    // Optional helper for UI
    public String getFormattedTimestamp() {
        return timestamp != null ? timestamp.toString().replace("T", " ").replace("Z", "") : "N/A";
    }
}
