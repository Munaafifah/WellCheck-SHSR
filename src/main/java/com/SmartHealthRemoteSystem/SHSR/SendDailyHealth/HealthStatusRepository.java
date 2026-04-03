// package com.SmartHealthRemoteSystem.SHSR.SendDailyHealth;

// import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorData;
// import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
// import com.SmartHealthRemoteSystem.SHSR.Repository.SubCollectionSHSRDAO;
// import com.google.api.core.ApiFuture;
// import com.google.cloud.Timestamp;
// import com.google.cloud.firestore.DocumentReference;
// import com.google.cloud.firestore.DocumentSnapshot;
// import com.google.cloud.firestore.Firestore;
// import com.google.cloud.firestore.WriteResult;
// import com.google.firebase.cloud.FirestoreClient;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Repository;

// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;
// import java.util.concurrent.ExecutionException;
// // import org.springframework.data.jpa.repository.JpaRepository;
// // import org.springframework.data.jpa.repository.Query;

// @Repository
// public class HealthStatusRepository implements SubCollectionSHSRDAO<HealthStatus> {
//     private final SHSRDAO<SensorData> sensorDataRepository;

//     public static final String COL_NAME = "Patient";
//     public static final String SUB_COL_NAME = "HealthStatus";

//     @Autowired
//     public HealthStatusRepository(SHSRDAO<SensorData> sensorDataRepository) {
//         this.sensorDataRepository = sensorDataRepository;
//     }

//     // @Query("SELECT h FROM HealthStatus h WHERE " + "CONCAT(h.timestamp, h.additionalNotes)" + "LIKE %?1%")
//     // public List<HealthStatus> search(String keyword) throws ExecutionException, InterruptedException;

//     @Override
//     public HealthStatus get(String patientId, String healthStatusId) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference documentReference = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document(healthStatusId);
//         ApiFuture<DocumentSnapshot> future = documentReference.get();
//         DocumentSnapshot document = future.get();
//         HealthStatus tempHealthStatus;
//         if (document.exists()) {
//             tempHealthStatus = document.toObject(HealthStatus.class);
//             return tempHealthStatus;
//         } else {
//             return null;
//         }
//     }

//     @Override
//     public List<HealthStatus> getAll(String patientId) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         Iterable<DocumentReference> documentReference = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).listDocuments();
//         Iterator<DocumentReference> iterator = documentReference.iterator();

//         List<HealthStatus> healthStatusList = new ArrayList<>();
//         HealthStatus healthStatus;
//         while (iterator.hasNext()) {
//             DocumentReference documentReference1=iterator.next();
//             ApiFuture<DocumentSnapshot> future = documentReference1.get();
//             DocumentSnapshot document = future.get();
//             healthStatus = document.toObject(HealthStatus.class);
//             healthStatusList.add(healthStatus);
//         }

//         return healthStatusList;
//     }

//     @Override
//     public String save(HealthStatus healthStatus, String patientId) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         //auto create data ID by firebase
//         DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document();
//         healthStatus.setHealthStatusId(addedDocRef.getId());
//         ApiFuture<WriteResult> collectionsApiFuture =
//                 addedDocRef.set(healthStatus);
//         ApiFuture<WriteResult> writeResult = addedDocRef.update("timestamp", collectionsApiFuture.get().getUpdateTime());

//         return collectionsApiFuture.get().getUpdateTime().toString();
//     }

//     @Override
//     public String update(HealthStatus healthStatus, String patientId) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document(healthStatus.getHealthStatusId());
//         ApiFuture<WriteResult> collectionsApiFuture = null;
//         if(!(healthStatus.getDoctorId().isEmpty())){
//             collectionsApiFuture = addedDocRef.update("doctorId", healthStatus.getDoctorId());
//         }
//         if (!(healthStatus.getAdditionalNotes().isEmpty())){
//             collectionsApiFuture = addedDocRef.update("additionalNotes", healthStatus.getDoctorId());
//         }
//         if (collectionsApiFuture != null) {
//             ApiFuture<WriteResult> writeResult = addedDocRef.update("timestamp", collectionsApiFuture.get().getUpdateTime());
//             return writeResult.get().getUpdateTime().toString();
//         }
//         return Timestamp.now().toString();
//     }

//     @Override
//     public String delete(String patientId, String healthStatusId) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         if(get(patientId, healthStatusId) == null){
//             return "Health status with Id " + healthStatusId + " is not exist.";
//         }
//         DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document(healthStatusId);
//         ApiFuture<WriteResult> writeResult = addedDocRef.delete();

//         return "Document with Health Id " + healthStatusId + " has been deleted";
//     }
// }

package com.SmartHealthRemoteSystem.SHSR.SendDailyHealth;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface HealthStatusRepository extends MongoRepository<HealthStatusDocumentWrapper, String> {
    List<HealthStatusDocumentWrapper> findByPatientId(String patientId);
}
