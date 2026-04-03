package com.SmartHealthRemoteSystem.SHSR.User.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
import com.SmartHealthRemoteSystem.SHSR.User.MongoUserRepository;
import com.SmartHealthRemoteSystem.SHSR.User.User;

@Repository
public class PatientRepository implements SHSRDAO<Patient> {
    private static final Logger LOGGER = Logger.getLogger(PatientRepository.class.getName());

    @Autowired
    private MongoPatientRepository mongoPatientRepository;

    @Autowired
    private MongoUserRepository mongoUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Safely retrieve a patient
    @Override
    public Patient get(String patientId) throws ExecutionException, InterruptedException {
        Optional<Patient> patientOptional = mongoPatientRepository.findById(patientId);

        if (patientOptional.isPresent()) {
            Patient tempPatient = patientOptional.get();
            try {
                Optional<User> userOpt = mongoUserRepository.findById(patientId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    tempPatient.setUserId(user.getUserId());
                    tempPatient.setName(user.getName());
                    tempPatient.setPassword(user.getPassword());
                    tempPatient.setContact(user.getContact());
                    tempPatient.setRole(user.getRole());
                    tempPatient.setEmail(user.getEmail());
                }
            } catch (Exception e) {
                LOGGER.warning("User details could not be retrieved for patient ID: " + patientId);
            }
            return tempPatient;
        }
        return null;
    }

    @Override
    public List<Patient> getAll() throws ExecutionException, InterruptedException {
        System.out.println("✅ PatientRepository: getAll() called");
        List<Patient> patientList = new ArrayList<>();
        List<Patient> allPatients = mongoPatientRepository.findAll();

        for (Patient patient : allPatients) {
            System.out.println("🔁 Checking patient: " + patient.getUserId());

            Optional<User> userOpt = mongoUserRepository.findById(patient.getUserId());

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("✅ User found: " + user.getUserId() + " - " + user.getName());

                patient.setName(user.getName());
                patient.setPassword(user.getPassword());
                patient.setContact(user.getContact());
                patient.setRole(user.getRole());
                patient.setEmail(user.getEmail());
            } else {
                System.out.println("❌ No user found for patient: " + patient.getUserId());
            }

            patientList.add(patient);
        }

        return patientList;
    }

    // ✅ Secure save
    @Override
    public String save(Patient patient) throws ExecutionException, InterruptedException {
        System.out.println("📝 Saving patient with ID: " + patient.getUserId());

        String password = patient.getPassword();

        // ✅ Only encode if not already encoded
        if (!password.startsWith("$2a$")) {
            password = passwordEncoder.encode(password);
            patient.setPassword(password);
        }

        // ✅ Save into Patient collection
        mongoPatientRepository.save(patient);

        // ✅ Save into User collection
        User user = new User(
            patient.getUserId(),
            patient.getName(),
            password,
            patient.getContact(),
            patient.getRole(),
            patient.getEmail()
        );
        mongoUserRepository.save(user);

        return "Patient saved successfully.";
    }

    // ✅ Secure update
    @Override
    public String update(Patient patient) throws ExecutionException, InterruptedException {
        Optional<Patient> existingPatientOpt = mongoPatientRepository.findById(patient.getUserId());

        if (existingPatientOpt.isPresent()) {
            Patient existingPatient = existingPatientOpt.get();

            // ✅ Update only allowed fields
            if (patient.getAddress() != null && !patient.getAddress().isEmpty()) {
                existingPatient.setAddress(patient.getAddress());
            }
            if (patient.getEmergencyContact() != null && !patient.getEmergencyContact().isEmpty()) {
                existingPatient.setEmergencyContact(patient.getEmergencyContact());
            }
            if (patient.getAssigned_doctor() != null) {
                existingPatient.setAssigned_doctor(patient.getAssigned_doctor());
            }
            if (patient.getSensorDataId() != null) {
                existingPatient.setSensorDataId(patient.getSensorDataId());
            }
            if (patient.getStatus() != null) {
                existingPatient.setStatus(patient.getStatus());
            }

            if (patient.getProfilePicture() != null) {
                existingPatient.setProfilePicture(patient.getProfilePicture());
            }
            if (patient.getProfilePictureType() != null) {
                existingPatient.setProfilePictureType(patient.getProfilePictureType());
            }

            existingPatient.setNeedsManualDiagnosis(patient.isNeedsManualDiagnosis());

            // ✅ Save patient
            mongoPatientRepository.save(existingPatient);

            // ✅ Also update user
            String password = patient.getPassword();
            if (!password.startsWith("$2a$")) {
                password = passwordEncoder.encode(password);
            }

            User user = new User(
                    patient.getUserId(),
                    patient.getName(),
                    password,
                    patient.getContact(),
                    patient.getRole(),
                    patient.getEmail()
            );
            mongoUserRepository.save(user);

            return "Patient updated successfully.";
        }

        return "Error: Patient not found.";
    }

    @Override
    public String delete(String id) throws ExecutionException, InterruptedException {
        if (mongoPatientRepository.existsById(id)) {
            mongoPatientRepository.deleteById(id);
            mongoUserRepository.deleteById(id);
            return "Patient with ID " + id + " deleted successfully.";
        }
        return "Error: Patient not found.";
    }

    public Optional<Patient> findByEmail(String email) {
        return mongoPatientRepository.findById(email);
    }
}
