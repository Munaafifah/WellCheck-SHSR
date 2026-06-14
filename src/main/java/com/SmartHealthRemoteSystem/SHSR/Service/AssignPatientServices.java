//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.PatientRepository;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.DoctorRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class AssignPatientServices implements IAssignPatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AssignPatientServices(PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // not in interface — no @Override needed
    public List<Patient> getListUnassignedPatients() throws ExecutionException, InterruptedException {
        return patientRepository.getAll().stream()
            .filter(p -> (p.getAssigned_doctor() == null || p.getAssigned_doctor().isEmpty())
                && !"Released".equalsIgnoreCase(p.getStatus()))
            .collect(Collectors.toList());
    }

    @Override
    public Doctor getDoctor(String doctorId) throws ExecutionException, InterruptedException {
        return doctorRepository.get(doctorId);
    }

    @Override
    public Patient getPatient(String patientId) throws ExecutionException, InterruptedException {
        return patientRepository.get(patientId);
    }

    @Override
    public void assignPatient(String patientId, String doctorId) throws ExecutionException, InterruptedException {
        Patient patient = getPatient(patientId);
        patient.setAssigned_doctor(doctorId);
        patient.setStatus("Under Surveillance");
        patientRepository.update(patient);
    }

    @Override
    public String UnassignDoctor(String patientId, String doctorId) throws ExecutionException, InterruptedException {
        Patient patient = patientRepository.get(patientId);

        if (patient != null && doctorId.equals(patient.getAssigned_doctor())) {
            patient.setAssigned_doctor("");
            patient.setStatus("Under Surveillance");
            return patientRepository.update(patient);
        }

        return "Error: Patient not found or not assigned to this doctor.";
    }

    @Override
    public void releasePatient(String patientId) throws ExecutionException, InterruptedException {
        Patient patient = getPatient(patientId);
        if (patient != null) {
            patient.setAssigned_doctor("");
            patient.setStatus("Released");
            patientRepository.update(patient);
        }
    }

    // not in interface — no @Override needed
    @Override
    public List<Patient> getAvailablePatients() throws ExecutionException, InterruptedException {
        return getListUnassignedPatients();
    }
}