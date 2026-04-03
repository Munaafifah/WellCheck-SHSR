package com.SmartHealthRemoteSystem.SHSR.Prediction;
import com.SmartHealthRemoteSystem.SHSR.Prediction.Prediction;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;

public class PredictionWrapper {
    private Patient patient;
    private Prediction prediction;

    public PredictionWrapper(Patient patient, Prediction prediction) {
        this.patient = patient;
        this.prediction = prediction;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Prediction getPrediction() {
        return prediction;
    }

    public void setPrediction(Prediction prediction) {
        this.prediction = prediction;
    }
}
