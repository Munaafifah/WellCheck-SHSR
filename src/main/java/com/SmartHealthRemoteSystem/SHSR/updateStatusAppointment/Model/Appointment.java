// File: Model/Appointment.java
package com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Model;

public class Appointment {
    private String id;
    private String appointmentId;
    private String userId;
    private String doctorId;
    private String hospitalId;
    private String appointmentDate;
    private String appointmentTime;
    private String duration;
    private String registeredHospital;
    private String typeOfSickness;
    private String additionalNotes;
    private String insuranceProvider;
    private String insurancePolicyNumber;
    private String email;
    private int appointmentCost;
    private String statusPayment;
    private String statusAppointment;
    private String timestamp;

    public Appointment(String id, String appointmentId, String userId, String doctorId, 
                      String hospitalId, String appointmentDate, String appointmentTime, 
                      String duration, String registeredHospital, String typeOfSickness, 
                      String additionalNotes, String insuranceProvider, String insurancePolicyNumber, 
                      String email, int appointmentCost, String statusPayment, 
                      String statusAppointment, String timestamp) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.doctorId = doctorId;
        this.hospitalId = hospitalId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.duration = duration;
        this.registeredHospital = registeredHospital;
        this.typeOfSickness = typeOfSickness;
        this.additionalNotes = additionalNotes;
        this.insuranceProvider = insuranceProvider;
        this.insurancePolicyNumber = insurancePolicyNumber;
        this.email = email;
        this.appointmentCost = appointmentCost;
        this.statusPayment = statusPayment;
        this.statusAppointment = statusAppointment;
        this.timestamp = timestamp;
    }

    // Getters
    public String getId() { return id; }
    public String getAppointmentId() { return appointmentId; }
    public String getUserId() { return userId; }
    public String getDoctorId() { return doctorId; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getDuration() { return duration; }
    public String getTypeOfSickness() { return typeOfSickness; }
    public String getAdditionalNotes() { return additionalNotes; }
    public String getInsurancePolicyNumber() { return insurancePolicyNumber; }
    public String getEmail() { return email; }
    public int getAppointmentCost() { return appointmentCost; }
    public String getStatusPayment() { return statusPayment; }
    public String getStatusAppointment() { return statusAppointment; }
    public String getTimestamp() { return timestamp; }
    public String getHospitalId() { return hospitalId; }
    public String getRegisteredHospital() { return registeredHospital; }
    public String getInsuranceProvider() { return insuranceProvider; }
}
