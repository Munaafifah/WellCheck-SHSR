// package com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription;

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
// public class PrescriptionRepository implements SubCollectionSHSRDAO<Prescription> {

//     public static final String COL_NAME = "Patient";
//     public static final String SUB_COL_NAME = "Prescription";

//     @Override
//     public Prescription get(String patientId, String prescriptionId) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference documentReference = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document(prescriptionId);
//         ApiFuture<DocumentSnapshot> future = documentReference.get();
//         DocumentSnapshot document = future.get();
//         Prescription tempPrescription;
//         if (document.exists()) {
//             tempPrescription = document.toObject(Prescription.class);
//             return tempPrescription;
//         } else {
//             return null;
//         }
//     }

//     @Override
//     public List<Prescription> getAll(String patientId) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         Iterable<DocumentReference> documentReference = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).listDocuments();
//         Iterator<DocumentReference> iterator = documentReference.iterator();

//         List<Prescription> prescriptionList = new ArrayList<>();
//         Prescription prescription;
//         while (iterator.hasNext()) {
//             DocumentReference documentReference1=iterator.next();
//             ApiFuture<DocumentSnapshot> future = documentReference1.get();
//             DocumentSnapshot document = future.get();
//             prescription = document.toObject(Prescription.class);
//             prescriptionList.add(prescription);
//         }

//         return prescriptionList;
//     }

//     @Override
//     public String save(Prescription prescription, String patientId) throws ExecutionException, InterruptedException {
//          Firestore dbFirestore = FirestoreClient.getFirestore();
//         //auto create data ID by firebase
//         DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document();
//         prescription.setPrescriptionId(addedDocRef.getId());
//         ApiFuture<WriteResult> collectionsApiFuture =
//                 addedDocRef.set(prescription);
//         ApiFuture<WriteResult> writeResult = addedDocRef.update("timestamp", collectionsApiFuture.get().getUpdateTime().toString());
//         return collectionsApiFuture.get().getUpdateTime().toString();
//     }

//     @Override
//     public String update(Prescription prescription, String patientId) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         DocumentReference addedDocRef = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document(prescription.getPrescriptionId());
//         ApiFuture<WriteResult> collectionsApiFuture;
//         if(!(prescription.getDoctorId().isEmpty())){
//             addedDocRef.update("doctorId", prescription.getDoctorId());
//         }
//         if (!(prescription.getPrescriptionDescription().isEmpty())){
//             addedDocRef.update("prescriptionDescription", prescription.getPrescriptionDescription());
//         }
//         if (!(prescription.getDiagnosisAilmentDescription().isEmpty())){
//             addedDocRef.update("diagnosisAilmentDescription", prescription.getDiagnosisAilmentDescription());
//         }
//         //need to make sure that user interface update all medicine list at once,
//         //as firestore does not support updating element of array as in 13/1/2022.
//         collectionsApiFuture = addedDocRef.update("medicineList", prescription.getMedicineList());
//         ApiFuture<WriteResult> writeResult = addedDocRef.update("timestamp", collectionsApiFuture.get().getUpdateTime().toString());
//         return collectionsApiFuture.get().getUpdateTime().toString();
//     }

//     @Override
//     public String delete(String patientId, String prescriptionId) throws ExecutionException, InterruptedException {
//         Firestore dbFirestore = FirestoreClient.getFirestore();
//         if(get(patientId, prescriptionId) != null){
//             return "Prescription with Id"  + prescriptionId +  "is not exist.";
//         }
//         ApiFuture<WriteResult> writeResult = dbFirestore.collection(COL_NAME).document(patientId).collection(SUB_COL_NAME).document(prescriptionId).delete();
//         return "Document with Sensor Data Id " + prescriptionId + " has been deleted";
//     }

//     /* public String saveMedicineList(List<String>medList, String patientId){
//         Firestore dbFirestore = FirestoreClient.getFirestore();

//     } */
// }
