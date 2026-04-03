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
// public class PredictionRepository implements SubCollectionSHSRDAO<Prediction>{
  
//   public static final String COL_NAME = "Patient";
//   public static final String SUB_COL_NAME = "Prediction";

//   @Override
//   public Prediction get(String patientId, String predictionID) throws ExecutionException, InterruptedException{
//            Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference documentReference = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document(predictionID);
//         ApiFuture<DocumentSnapshot> future = documentReference.get();
//         DocumentSnapshot document = future.get();
//         Prediction tempPrediction;
//         if (document.exists()) {
//           tempPrediction = document.toObject(Prediction.class);
//             return tempPrediction;
//         } else {
//             return null;
//         }
//   }

//   @Override
//   public List<Prediction> getAll(String patientId) throws ExecutionException, InterruptedException{
//     Firestore dbFirestore = FirestoreClient.getFirestore();
//     Iterable<DocumentReference> documentReference = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).listDocuments();
//     Iterator<DocumentReference> iterator = documentReference.iterator();

//     List<Prediction> predictionList = new ArrayList<>();
//     Prediction prediction;
//     while (iterator.hasNext()){
//       DocumentReference documentReference1=iterator.next();
//       ApiFuture<DocumentSnapshot> future = documentReference1.get();
//       DocumentSnapshot document = future.get();
//       prediction = document.toObject(Prediction.class);
//       predictionList.add(prediction);
//     }

//     return predictionList;
//   }

//   @Override
//   public String save(Prediction prediction, String patientId) throws ExecutionException, InterruptedException{
//     Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document();
//         prediction.setPredictionID(addedDocRef.getId());
//         ApiFuture<WriteResult> collectionsApiFuture = addedDocRef.set(prediction);
//         ApiFuture<WriteResult> writeResult = addedDocRef.update("timestamp", collectionsApiFuture.get().getUpdateTime().toString());
//         return collectionsApiFuture.get().getUpdateTime().toString();
//   }

//   @Override
//   public String update(Prediction prediction, String patientId) throws ExecutionException, InterruptedException{
//     Firestore dbFirestore = FirestoreClient.getFirestore();
//     DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document(prediction.getPredictionID());
//     ApiFuture<WriteResult> collectionsApiFuture = addedDocRef.update("symptomsList", prediction.getSymptomsList());
//     addedDocRef.update("diagnosisList", prediction.getSymptomsList());
//     addedDocRef.update("probabilityList", prediction.getSymptomsList());
//     ApiFuture<WriteResult> writeResult = addedDocRef.update("timestamp", collectionsApiFuture.get().getUpdateTime().toString());
//     return collectionsApiFuture.get().getUpdateTime().toString();
//   }

//   @Override
//   public String delete(String patientId, String predictionId) throws ExecutionException, InterruptedException{
//     Firestore dbFirestore = FirestoreClient.getFirestore();
//         if(get(patientId, predictionId) != null){
//             return "Prediction with Id"  + predictionId +  "is not exist.";
//         }
//         ApiFuture<WriteResult> writeResult = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document(predictionId).delete();
//         return "Document with Sensor Data Id " + predictionId + " has been deleted";
//   }
// }
