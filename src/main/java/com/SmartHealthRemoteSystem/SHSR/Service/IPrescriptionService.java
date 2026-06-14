package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription.Prescription;

import java.util.List;
import java.util.Map;

/**
 * @interface IPrescriptionService
 * Provided interface contract for prescription management.
 * PrescriptionService realizes this interface.
 */
public interface IPrescriptionService {

    String createPrescription(String patientId, Prescription prescription);

    List<Prescription> getAllPrescriptions(String patientId);

    Prescription getPrescription(String patientId, String prescriptionId);

    String updatePrescription(String patientId, Prescription updatedPrescription);

    String deletePrescription(String patientId, String prescriptionId);

    Map<String, Object> prescribeMedicines(String patientId,
            Map<String, Integer> selectedMedicines,
            String prescriptionDescription,
            String diagnosisAilmentDescription);

    void linkAppointmentToPrescription(String patientId, String prescriptionId, String appointmentId);

    List<Prescription> getRecentPrescriptionsByDoctor(String doctorId, int limit);
}