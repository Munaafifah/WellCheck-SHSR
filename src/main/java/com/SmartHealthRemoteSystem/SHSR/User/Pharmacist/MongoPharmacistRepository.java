package com.SmartHealthRemoteSystem.SHSR.User.Pharmacist;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoPharmacistRepository extends MongoRepository<Pharmacist, String> {
    Optional<Pharmacist> findByEmail(String email);
}
