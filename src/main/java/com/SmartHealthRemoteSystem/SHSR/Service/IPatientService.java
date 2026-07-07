package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * @interface IPatientService
 *            Provided interface contract for patient management.
 *            PatientService realizes this interface.
 */
public interface IPatientService {

    String createPatient(Patient patient) throws ExecutionException, InterruptedException;

    Optional<Patient> getPatientByEmail(String email);

    Patient getPatientById(String id) throws ExecutionException, InterruptedException;

    List<Patient> getAllPatients() throws ExecutionException, InterruptedException;

    String updatePatient(Patient patient) throws ExecutionException, InterruptedException;

    String deletePatient(String id) throws ExecutionException, InterruptedException;

    String getPatientSensorId(String patientId) throws ExecutionException, InterruptedException;

    Doctor getDoctor(String doctorId) throws ExecutionException, InterruptedException;

    String resetPatientPassword(String patientId, String newPlainPassword)
            throws ExecutionException, InterruptedException;
}