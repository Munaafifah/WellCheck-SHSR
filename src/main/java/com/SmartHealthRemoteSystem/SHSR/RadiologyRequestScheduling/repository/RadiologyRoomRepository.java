package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RadiologyRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for RadiologyRoom documents.
 * Backs the room-availability dropdown shown to Radiographers on the
 * Schedule Appointment page (UCR010).
 */
@Repository
public interface RadiologyRoomRepository extends MongoRepository<RadiologyRoom, String> {

    // All active rooms for a given modality (e.g. "CT", "MRI")
    List<RadiologyRoom> findByModalityAndIsActiveTrue(String modality);

    // All rooms regardless of active flag — useful for an admin management screen
    List<RadiologyRoom> findByModality(String modality);
}