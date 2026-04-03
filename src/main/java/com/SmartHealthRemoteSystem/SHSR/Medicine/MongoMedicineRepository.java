package com.SmartHealthRemoteSystem.SHSR.Medicine;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoMedicineRepository extends MongoRepository<Medicine, String> {
    // You can add custom methods if needed later
    List<Medicine> findByPatientId(String patientId);

}
