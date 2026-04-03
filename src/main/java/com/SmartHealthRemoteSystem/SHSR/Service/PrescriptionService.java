

//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.Medicine.Medicine;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.MongoPatientRepository;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription.Prescription;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class PrescriptionService {

    @Autowired
    private MongoPatientRepository patientRepository;

    @Autowired
    private MedicineService medicineService;

    // ✅ Create and save a prescription
    public String createPrescription(String patientId, Prescription prescription) {
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        if (!patientOptional.isPresent()) {
            return "Patient not found!";
        }

        Patient patient = patientOptional.get();

        // Generate ID and set timestamp
        String prescriptionId = UUID.randomUUID().toString();
        prescription.setPrescriptionId(prescriptionId);
        prescription.setTimestamp(Instant.now());


        // Save into embedded map
        Map<String, Prescription> currentPrescriptions = patient.getPrescription();
        currentPrescriptions.put(prescriptionId, prescription);
        patient.setPrescription(currentPrescriptions);

        patientRepository.save(patient);

        return prescriptionId;
    }

    // ✅ Retrieve all prescriptions for a patient
    public List<Prescription> getAllPrescriptions(String patientId) {
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        if (!patientOptional.isPresent()) return Collections.emptyList();

        Patient patient = patientOptional.get();
        return new ArrayList<>(patient.getPrescription().values());
    }

    // ✅ Get single prescription
    public Prescription getPrescription(String patientId, String prescriptionId) {
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        if (!patientOptional.isPresent()) return null;

        return patientOptional.get().getPrescription().get(prescriptionId);
    }

    // ✅ Update prescription
    public String updatePrescription(String patientId, Prescription updatedPrescription) {
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        if (!patientOptional.isPresent()) return "Patient not found.";

        Patient patient = patientOptional.get();
        Map<String, Prescription> prescriptionMap = patient.getPrescription();

        if (!prescriptionMap.containsKey(updatedPrescription.getPrescriptionId())) {
            return "Prescription ID not found.";
        }

        updatedPrescription.setTimestamp(Instant.now());
        prescriptionMap.put(updatedPrescription.getPrescriptionId(), updatedPrescription);
        patient.setPrescription(prescriptionMap);
        patientRepository.save(patient);

        return "Prescription updated successfully.";
    }

    // ✅ Delete prescription
    public String deletePrescription(String patientId, String prescriptionId) {
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        if (!patientOptional.isPresent()) return "Patient not found.";

        Patient patient = patientOptional.get();
        Map<String, Prescription> prescriptionMap = patient.getPrescription();
        if (prescriptionMap.remove(prescriptionId) != null) {
            patient.setPrescription(prescriptionMap);
            patientRepository.save(patient);
            return "Prescription deleted.";
        }

        return "Prescription not found.";
    }

    // ✅ Convenience method for prescribing medicines
    public String prescribeMedicines(String patientId, Map<String, Integer> selectedMedicines,
                                     String prescriptionDescription, String diagnosisAilmentDescription) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        String doctorId = userDetails.getUsername();

        List<String> medicineDetailsList = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : selectedMedicines.entrySet()) {
            Medicine medicine = medicineService.getMedicine(entry.getKey());
            if (medicine != null) {
                medicineService.prescribeMedicine(patientId, medicine.getMedId(), entry.getValue());
                medicineDetailsList.add(medicine.getMedName() + " - Quantity: " + entry.getValue());
            }
        }

        Prescription prescription = new Prescription(doctorId, medicineDetailsList,
                prescriptionDescription, diagnosisAilmentDescription);

        return createPrescription(patientId, prescription);
    }
}

