package com.SmartHealthRemoteSystem.SHSR.User.Radiologist;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoRadiologistRepository extends MongoRepository<Radiologist, String> {
}
