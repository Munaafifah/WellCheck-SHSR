package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @interface IAssignPatientService
 *            Required interface contract for patient assignment management.
 *            AssignPatientServices realizes this interface.
 */
public interface IAssignPatientService {

    Doctor getDoctor(String doctorId) throws ExecutionException, InterruptedException;

    Patient getPatient(String patientId) throws ExecutionException, InterruptedException;

    void assignPatient(String patientId, String doctorId) throws ExecutionException, InterruptedException;

    String UnassignDoctor(String patientId, String doctorId) throws ExecutionException, InterruptedException;

    void releasePatient(String patientId) throws ExecutionException, InterruptedException;

    List<Patient> getAvailablePatients() throws ExecutionException, InterruptedException;
}