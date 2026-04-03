
//MongoDB//

package com.SmartHealthRemoteSystem.SHSR.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.DoctorRepository;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.PatientRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public Doctor getDoctor(String doctorId) throws ExecutionException, InterruptedException {
        return doctorRepository.get(doctorId);
    }

    public List<Doctor> getAllDoctors() throws ExecutionException, InterruptedException {
        return doctorRepository.getAll();
    }

    public String saveDoctor(Doctor doctor) throws ExecutionException, InterruptedException {
        return doctorRepository.save(doctor);
    }

    public String updateDoctor(Doctor doctor) throws ExecutionException, InterruptedException {
        return doctorRepository.update(doctor);
    }

    public String deleteDoctor(String id) throws ExecutionException, InterruptedException {
        return doctorRepository.delete(id);
    }

    public List<Patient> getListPatient() throws ExecutionException, InterruptedException {
        return patientRepository.getAll();
    }

    // ADD THIS: Get only patients assigned to this doctor
    public List<Patient> findAllPatientAssignToDoctor(String doctorId) throws ExecutionException, InterruptedException {
        List<Patient> allPatients = patientRepository.getAll(); // fetch all patients
    
        List<Patient> assignedPatients = new ArrayList<>();
        for (Patient patient : allPatients) {
            if (patient.getAssigned_doctor() != null && patient.getAssigned_doctor().equals(doctorId)) {
                assignedPatients.add(patient); // only keep the patients assigned to this doctor
            }
        }
    
        return assignedPatients;
    }

    public Patient getPatient(String patientId) throws ExecutionException, InterruptedException {
       return patientRepository.get(patientId);
     }


     public Doctor getDoctorById(String doctorId) {
    return doctorRepository.get(doctorId);
}

    
}
