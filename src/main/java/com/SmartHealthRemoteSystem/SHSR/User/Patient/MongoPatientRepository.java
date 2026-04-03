package com.SmartHealthRemoteSystem.SHSR.User.Patient;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// @Repository
// public interface MongoPatientRepository extends MongoRepository<Patient, String> {
//     Optional<Patient> findByEmail(String email);
// }

@Repository
public interface MongoPatientRepository extends MongoRepository<Patient, String> {
    List<Patient> findAll(); // this will now only return documents with _class = Patient
}

