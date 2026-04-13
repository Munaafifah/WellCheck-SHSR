package com.SmartHealthRemoteSystem.SHSR.User.Radiographer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoRadiographerRepository extends MongoRepository<Radiographer, String> {
}
