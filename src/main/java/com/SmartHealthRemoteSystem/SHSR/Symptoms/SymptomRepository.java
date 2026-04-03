// package com.SmartHealthRemoteSystem.SHSR.Symptoms;

// import com.SmartHealthRemoteSystem.SHSR.Repository.SHSRDAO;
// import com.google.api.core.ApiFuture;
// import com.google.cloud.firestore.DocumentReference;
// import com.google.cloud.firestore.DocumentSnapshot;
// import com.google.cloud.firestore.Firestore;
// import com.google.cloud.firestore.WriteResult;
// import com.google.firebase.cloud.FirestoreClient;
// import org.springframework.stereotype.Repository;

// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;
// import java.util.concurrent.ExecutionException;

// @Repository
// public class SymptomsRepository implements SHSRDAO<Symptoms> {
//     private final String COL_NAME = "Symptoms";

//     @Override
//     public Symptoms get(String symptomID) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference documentReference = dbFirestore.collection(COL_NAME).document(symptomID);
//         ApiFuture<DocumentSnapshot> future = documentReference.get();
//         DocumentSnapshot document = future.get();
//         if (document.exists()) {
//             return document.toObject(Symptoms.class);
//         } else {
//             return null;
//         }
//     }

//     @Override
//     public List<Symptoms> getAll() throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         Iterable<DocumentReference> documentReference = dbFirestore.collection(COL_NAME).listDocuments();
//         Iterator<DocumentReference> iterator = documentReference.iterator();

//         List<Symptoms> symptomsList = new ArrayList<>();
//         while (iterator.hasNext()) {
//             DocumentReference docRef = iterator.next();
//             ApiFuture<DocumentSnapshot> future = docRef.get();
//             DocumentSnapshot document = future.get();
//             if (document.exists()) {
//                 symptomsList.add(document.toObject(Symptoms.class));
//             }
//         }
//         return symptomsList;
//     }

//     @Override
//     public String save(Symptoms symptom) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document();
//         symptom.setSymptomID(addedDocRef.getId());
//         ApiFuture<WriteResult> collectionsApiFuture = addedDocRef.set(symptom);
//         return addedDocRef.getId();
//     }

//     @Override
//     public String update(Symptoms symptom) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference docRef = dbFirestore.collection(COL_NAME).document(symptom.getSymptomID());

//         List<ApiFuture<WriteResult>> updateTasks = new ArrayList<>();
//         if (!symptom.getName().isEmpty()) {
//             updateTasks.add(docRef.update("name", symptom.getName()));
//         }
//         if (!symptom.getValue().isEmpty()) {
//             updateTasks.add(docRef.update("value", symptom.getValue()));
//         }

//         for (ApiFuture<WriteResult> task : updateTasks) {
//             task.get();
//         }

//         return "Update successful";
//     }

//     @Override
//     public String delete(String symptomID) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         if (get(symptomID) == null) {
//             return "The symptom with ID " + symptomID + " does not exist.";
//         }
//         ApiFuture<WriteResult> writeResult = dbFirestore.collection(COL_NAME).document(symptomID).delete();
//         return "Document with symptom ID " + symptomID + " has been deleted";
//     }
// }


//MongoDB//

package com.SmartHealthRemoteSystem.SHSR.Symptoms;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymptomRepository extends MongoRepository<Symptom, String> {
    
    // existing
    Symptom findByName(String name);
    
    // new: add case-insensitive search
    Symptom findByNameIgnoreCase(String name);
}
