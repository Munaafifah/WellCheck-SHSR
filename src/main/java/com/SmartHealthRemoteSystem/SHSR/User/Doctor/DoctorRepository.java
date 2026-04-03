//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.User.Doctor;

import com.SmartHealthRemoteSystem.SHSR.User.MongoUserRepository;
import com.SmartHealthRemoteSystem.SHSR.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DoctorRepository {

    @Autowired
    private MongoDoctorRepository mongoDoctorRepository;

    @Autowired
    private MongoUserRepository mongoUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // ✅ Get one doctor by ID
    public Doctor get(String doctorId) {
        Optional<Doctor> doctorOpt = mongoDoctorRepository.findById(doctorId);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            // Merge user info
            mongoUserRepository.findById(doctorId).ifPresent(user -> {
                doctor.setName(user.getName());
                doctor.setPassword(user.getPassword());
                doctor.setContact(user.getContact());
                doctor.setRole(user.getRole());
                doctor.setEmail(user.getEmail());
            });
            return doctor;
        }
        return null;
    }

    // ✅ Get all doctors
    public List<Doctor> getAll() {
        List<Doctor> doctors = new ArrayList<>();
        for (Doctor doctor : mongoDoctorRepository.findAll()) {
            mongoUserRepository.findById(doctor.getUserId()).ifPresent(user -> {
                doctor.setName(user.getName());
                doctor.setPassword(user.getPassword());
                doctor.setContact(user.getContact());
                doctor.setRole(user.getRole());
                doctor.setEmail(user.getEmail());
            });
            doctors.add(doctor);
        }
        return doctors;
    }

    // // ✅ Save doctor
    // public String save(Doctor doctor) {
    //     User user = new User(doctor.getUserId(), doctor.getName(), doctor.getPassword(), doctor.getContact(), doctor.getRole(), doctor.getEmail());
    //     mongoUserRepository.save(user);
    //     mongoDoctorRepository.save(doctor);
    //     return doctor.getUserId();
    // }

    public String save(Doctor doctor) {
    String encodedPassword = passwordEncoder.encode(doctor.getPassword());
    User user = new User(doctor.getUserId(), doctor.getName(), encodedPassword, doctor.getContact(), doctor.getRole(), doctor.getEmail());
    mongoUserRepository.save(user);
    mongoDoctorRepository.save(doctor);
    return doctor.getUserId();
    }

    // ✅ Update doctor
    public String update(Doctor doctor) {
        User user = new User(doctor.getUserId(), doctor.getName(), doctor.getPassword(), doctor.getContact(), doctor.getRole(), doctor.getEmail());
        mongoUserRepository.save(user);
        mongoDoctorRepository.save(doctor);
        return doctor.getUserId();
    }

    // ✅ Delete doctor
    public String delete(String doctorId) {
        mongoDoctorRepository.deleteById(doctorId);
        mongoUserRepository.deleteById(doctorId);
        return doctorId;
    }
}
