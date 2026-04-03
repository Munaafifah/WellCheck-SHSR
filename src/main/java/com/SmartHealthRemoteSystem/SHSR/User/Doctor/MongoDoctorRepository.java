package com.SmartHealthRemoteSystem.SHSR.User.Doctor;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoDoctorRepository extends MongoRepository<Doctor, String> {
    Optional<Doctor> findByEmail(String email);
}
