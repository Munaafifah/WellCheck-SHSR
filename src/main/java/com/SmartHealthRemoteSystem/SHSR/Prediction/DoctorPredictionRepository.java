// package com.SmartHealthRemoteSystem.SHSR.Prediction;

// import com.SmartHealthRemoteSystem.SHSR.Repository.SubCollectionSHSRDAO;
// import com.google.api.core.ApiFuture;
// import com.google.cloud.firestore.DocumentReference;
// import com.google.cloud.firestore.DocumentSnapshot;
// import com.google.cloud.firestore.Firestore;
// import com.google.cloud.firestore.WriteResult;
// import com.google.firebase.cloud.FirestoreClient;
// import org.springframework.stereotype.Repository;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;
// import java.util.concurrent.ExecutionException;

// @Repository
// public class DoctorPredictionRepository implements SubCollectionSHSRDAO<DoctorPrediction>{
//   public static final String COL_NAME = "Doctor";
//   public static final String SUB_COL_NAME = "Prediction";

//   @Override
//   public DoctorPrediction get(String doctorId, String predictionID) throws ExecutionException, InterruptedException{
//            Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference documentReference = dbFirestore.collection(COL_NAME).document(doctorId).collection(SUB_COL_NAME).document(predictionID);
//         ApiFuture<DocumentSnapshot> future = documentReference.get();
//         DocumentSnapshot document = future.get();
//         DoctorPrediction tempPrediction;
//         if (document.exists()) {
//           tempPrediction = document.toObject(DoctorPrediction.class);
//             return tempPrediction;
//         } else {
//             return null;
//         }
//   }

//   @Override
//   public List<DoctorPrediction> getAll(String doctorId) throws ExecutionException, InterruptedException{
//     Firestore dbFirestore = FirestoreClient.getFirestore();
//     Iterable<DocumentReference> documentReference = dbFirestore.collection(COL_NAME).document(doctorId).collection(SUB_COL_NAME).listDocuments();
//     Iterator<DocumentReference> iterator = documentReference.iterator();

//     List<DoctorPrediction> predictionList = new ArrayList<>();
//     DoctorPrediction prediction;
//     while (iterator.hasNext()){
//       DocumentReference documentReference1=iterator.next();
//       ApiFuture<DocumentSnapshot> future = documentReference1.get();
//       DocumentSnapshot document = future.get();
//       prediction = document.toObject(DoctorPrediction.class);
//       predictionList.add(prediction);
//     }

//     return predictionList;
//   }

//   @Override
//   public String save(DoctorPrediction prediction, String doctorId) throws ExecutionException, InterruptedException{
//     Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document(doctorId).collection(SUB_COL_NAME).document();
//         prediction.setPredictionID(addedDocRef.getId());
//         ApiFuture<WriteResult> collectionsApiFuture = addedDocRef.set(prediction);
//         ApiFuture<WriteResult> writeResult = addedDocRef.update("timestamp", collectionsApiFuture.get().getUpdateTime().toString());
//         return collectionsApiFuture.get().getUpdateTime().toString();
//   }

//   @Override
//   public String update(DoctorPrediction prediction, String doctorId) throws ExecutionException, InterruptedException{
//     Firestore dbFirestore = FirestoreClient.getFirestore();
//     DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document(doctorId).collection(SUB_COL_NAME).document(prediction.getPredictionID());
//     ApiFuture<WriteResult> collectionsApiFuture = addedDocRef.update("symptomsList", prediction.getSymptomsList());
//     addedDocRef.update("diagnosisList", prediction.getSymptomsList());
//     addedDocRef.update("probabilityList", prediction.getSymptomsList());
//     ApiFuture<WriteResult> writeResult = addedDocRef.update("timestamp", collectionsApiFuture.get().getUpdateTime().toString());
//     return collectionsApiFuture.get().getUpdateTime().toString();
//   }

//   @Override
//   public String delete(String doctorId, String predictionId) throws ExecutionException, InterruptedException{
//     Firestore dbFirestore = FirestoreClient.getFirestore();
//         if(get(doctorId, predictionId) != null){
//             return "Prediction with Id"  + predictionId +  "is not exist.";
//         }
//         ApiFuture<WriteResult> writeResult = dbFirestore.collection(COL_NAME).document(doctorId).collection(SUB_COL_NAME).document(predictionId).delete();
//         return "Document with Sensor Data Id " + predictionId + " has been deleted";
//   }
// }
