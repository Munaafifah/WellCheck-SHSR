package com.SmartHealthRemoteSystem.SHSR.DoctorSchedule;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorScheduleRepository extends MongoRepository<DoctorSchedule, String> {
    // MongoRepository gives us findAll(), findById(), save(), deleteById() for free
    // String = type of @Id (doctorId)
}