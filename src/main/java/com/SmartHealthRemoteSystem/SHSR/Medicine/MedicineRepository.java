// package com.SmartHealthRemoteSystem.SHSR.Medicine;

// import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
// import com.SmartHealthRemoteSystem.SHSR.Repository.SubCollectionSHSRDAO;
// import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
// import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
// import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
// import com.SmartHealthRemoteSystem.SHSR.User.Patient.PatientRepository;
// import com.SmartHealthRemoteSystem.SHSR.User.User;
// import com.google.api.core.ApiFuture;
// import com.google.cloud.Timestamp;
// import com.google.cloud.firestore.DocumentReference;
// import com.google.cloud.firestore.DocumentSnapshot;
// import com.google.cloud.firestore.Firestore;
// import com.google.cloud.firestore.WriteResult;
// import com.google.firebase.cloud.FirestoreClient;
// import com.google.firebase.database.*;
// import lombok.SneakyThrows;
// import org.springframework.stereotype.Repository;

// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;
// import java.util.concurrent.ExecutionException;

// @Repository
// public class MedicineRepository implements SHSRDAO<Medicine> {
//     private final String COL_NAME = "Medicine";

//     @Override
//     public Medicine get(String MedId) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference documentReference = dbFirestore.collection(COL_NAME).document(MedId);
//         ApiFuture<DocumentSnapshot> future = documentReference.get();
//         DocumentSnapshot document = future.get();
//         Medicine tempMedicine;
//         if (document.exists()) {
//             tempMedicine = document.toObject(Medicine.class);
//             return tempMedicine;
//         } else {
//             return null;
//         }
//     }

//     @Override
//     public List<Medicine> getAll() throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         Iterable<DocumentReference> documentReference = dbFirestore.collection(COL_NAME).listDocuments();
//         Iterator<DocumentReference> iterator = documentReference.iterator();

//         List<Medicine> medicineList = new ArrayList<>();
//         Medicine medicine;
//         while (iterator.hasNext()) {
//             DocumentReference documentReference1=iterator.next();
//             ApiFuture<DocumentSnapshot> future = documentReference1.get();
//             DocumentSnapshot document = future.get();
//             medicine = document.toObject(Medicine.class);
//             medicineList.add(medicine);
//         }
//         return medicineList;
//     }

//     @Override
//     public String save(Medicine medicine) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         //auto create data ID by firebase
//         DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document();
//         medicine.setMedId(addedDocRef.getId());
//         ApiFuture<WriteResult> collectionsApiFuture =
//                 addedDocRef.set(medicine);

//         /* ApiFuture<WriteResult> writeResult = addedDocRef.update("timestamp", collectionsApiFuture.get().getUpdateTime()); */

//         return addedDocRef.getId();
//     }

//     @Override
//     public String update(Medicine medicine) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document(medicine.getMedId());
    
//         // Create a list to hold update tasks
//         List<ApiFuture<WriteResult>> updateTasks = new ArrayList<>();
    
//         // Update medName only if it is not empty
//         if (!(medicine.getMedName().isEmpty())) {
//             updateTasks.add(addedDocRef.update("medName", medicine.getMedName()));
//         }
    
//         // Update Quantity
//         updateTasks.add(addedDocRef.update("quantity", medicine.getQuantity()));
    
//         // Wait for all update tasks to complete
//         for (ApiFuture<WriteResult> task : updateTasks) {
//             task.get(); // This will block and wait for each update to complete
//         }
    
//         return Timestamp.now().toString();
//     }

//     @Override
//     public String delete(String MedId) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         if(get(MedId) == null){
//             return "The medicine with Id " + MedId + " is not exist.";
//         }
//         ApiFuture<WriteResult> writeResult = dbFirestore.collection(COL_NAME).document(MedId).delete();
//         return "Document with medicine id " + MedId + " has been deleted";
//     }
// }

