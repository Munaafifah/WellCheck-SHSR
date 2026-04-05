package com.SmartHealthRemoteSystem.SHSR.User.ClinicAssistant;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoClinicAssistantRepository extends MongoRepository<ClinicAssistant, String> {
}