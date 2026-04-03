//MongoDB//

package com.SmartHealthRemoteSystem.SHSR.User.Doctor;

import org.springframework.data.mongodb.core.mapping.Document;
import com.SmartHealthRemoteSystem.SHSR.Prediction.DoctorPrediction;
import com.SmartHealthRemoteSystem.SHSR.User.User;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Pattern;

@Document(collection = "Doctor")
public class Doctor extends User {

    private String hospital;
    private String position;
    @Pattern(regexp = "image/.*", message = "Only image files are allowed")
    private String profilePicture;
    private String profilePictureType;

    // ✅ Embedded map for predictions
    private Map<String, DoctorPrediction> doctorPrediction = new HashMap<>();

    public Doctor() {}

    public Doctor(String userId, String name, String password, String contact, String role, String email,
                  String hospital, String position) {
        super(userId, name, password, contact, role, email);
        this.hospital = hospital;
        this.position = position;
    }

    public Doctor(String name, String password, String contact, String role,
                  String hospital, String position, String email) {
        super(name, password, contact, role, email, email); // ✅ FIXED
        this.hospital = hospital;
        this.position = position;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    

    public Map<String, DoctorPrediction> getDoctorPrediction() {
        return doctorPrediction;
    }

    public void setDoctorPrediction(Map<String, DoctorPrediction> doctorPrediction) {
        this.doctorPrediction = doctorPrediction;
    }

    public String getProfilePictureType() {
        return profilePictureType;
    }

    public void setProfilePictureType(String profilePictureType) {
        this.profilePictureType = profilePictureType;
    }
}
