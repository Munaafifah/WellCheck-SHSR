// package com.SmartHealthRemoteSystem.SHSR.Shsr.ApplicationService;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.google.auth.oauth2.GoogleCredentials;
// import com.google.cloud.firestore.Firestore;
// import com.google.firebase.FirebaseApp;
// import com.google.firebase.FirebaseOptions;
// import com.google.firebase.cloud.FirestoreClient;
// import com.google.gson.Gson;
// import org.apache.commons.io.IOUtils;
// import org.springframework.core.env.Environment;
// import org.springframework.stereotype.Service;

// import javax.annotation.PostConstruct;
// import java.io.IOException;
// import java.io.InputStream;

// @Service
// public class FirebaseInitializer {
//     private final Environment environment;

//     public FirebaseInitializer(Environment environment) {
//         this.environment = environment;
//     }

//     @PostConstruct
//     private void initDB() throws IOException {
//         FirebaseCredential firebaseCredential = new FirebaseCredential();
//         Gson gson = new Gson();

//         //private key
//         firebaseCredential.setType(environment.getRequiredProperty("FIREBASE_TYPE"));
//         String privateKey = environment.getRequiredProperty("FIREBASE_PRIVATE_KEY").replace("\\n", "\n");
//         firebaseCredential.setProject_id(environment.getRequiredProperty("FIREBASE_PROJECT_ID"));
//         firebaseCredential.setPrivate_key_id(environment.getRequiredProperty("FIREBASE_PRIVATE_KEY_ID"));
//         firebaseCredential.setPrivate_key(privateKey);
//         firebaseCredential.setClient_email(environment.getRequiredProperty("FIREBASE_CLIENT_EMAIL"));
//         firebaseCredential.setClient_id(environment.getRequiredProperty("FIREBASE_CLIENT_ID"));
//         firebaseCredential.setAuth_uri(environment.getRequiredProperty("FIREBASE_AUTH_URI"));
//         firebaseCredential.setToken_uri(environment.getRequiredProperty("FIREBASE_TOKEN_URI"));
//         firebaseCredential.setAuth_provider_x509_cert_url(environment.getRequiredProperty("FIREBASE_AUTH_PROVIDER_X509_CERT_URL"));
//         firebaseCredential.setClient_x509_cert_url(environment.getRequiredProperty("FIREBASE_CLIENT_X509_CERT_URL"));

//         ObjectMapper mapper = new ObjectMapper();
//         String jsonString = gson.toJson(firebaseCredential);

//         InputStream serviceAccount = IOUtils.toInputStream(jsonString);

//         FirebaseOptions options = new FirebaseOptions.Builder()
//                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                 .setDatabaseUrl("https://smarthealthcaresupport-default-rtdb.asia-southeast1.firebasedatabase.app")
//                 .build();
//         if(FirebaseApp.getApps().isEmpty()){
//             FirebaseApp.initializeApp(options);
//         }
//     }

//     public Firestore getFirebase(){
//         return FirestoreClient.getFirestore();
//     }
// }
