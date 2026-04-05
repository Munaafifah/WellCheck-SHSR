package com.SmartHealthRemoteSystem.SHSR.User.ClinicAssistant;

import org.springframework.data.mongodb.core.mapping.Document;
import com.SmartHealthRemoteSystem.SHSR.User.User;

@Document(collection = "ClinicAssistant")
public class ClinicAssistant extends User {

    private String clinic;
    private String position;
    private String profilePicture;
    private String profilePictureType;

    public ClinicAssistant() {}

    public ClinicAssistant(String userId, String name, String password, String contact,
                           String role, String email, String clinic, String position) {
        super(userId, name, password, contact, role, email);
        this.clinic = clinic;
        this.position = position;
    }

    public String getClinic() { return clinic; }
    public void setClinic(String clinic) { this.clinic = clinic; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getProfilePictureType() { return profilePictureType; }
    public void setProfilePictureType(String profilePictureType) { this.profilePictureType = profilePictureType; }
}