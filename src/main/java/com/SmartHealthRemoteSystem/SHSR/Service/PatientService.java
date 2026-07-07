//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.Service;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.DoctorRepository;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class PatientService implements IPatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String createPatient(Patient patient) throws ExecutionException, InterruptedException {
        return patientRepository.save(patient);
    }

    @Override
    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    @Override
    public Patient getPatientById(String id) throws ExecutionException, InterruptedException {
        return patientRepository.get(id);
    }

    @Override
    public List<Patient> getAllPatients() throws ExecutionException, InterruptedException {
        System.out.println("[PatientService] getAllPatients() called");
        return patientRepository.getAll();
    }

    @Override
    public String updatePatient(Patient patient) throws ExecutionException, InterruptedException {
        return patientRepository.update(patient);
    }

    @Override
    public String deletePatient(String id) throws ExecutionException, InterruptedException {
        return patientRepository.delete(id);
    }

    @Override
    public String getPatientSensorId(String patientId) throws ExecutionException, InterruptedException {
        Patient patient = patientRepository.get(patientId);
        return patient.getSensorDataId();
    }

    @Override
    public Doctor getDoctor(String doctorId) throws ExecutionException, InterruptedException {
        return doctorRepository.get(doctorId);
    }

    @Override
    public String resetPatientPassword(String patientId, String newPlainPassword) throws ExecutionException, InterruptedException {
        Patient patient = patientRepository.get(patientId);
        if (patient == null) {
            return "❌ Patient not found: " + patientId;
        }

        String encodedPassword = passwordEncoder.encode(newPlainPassword);
        patient.setPassword(encodedPassword);

        return patientRepository.save(patient);
    }
}