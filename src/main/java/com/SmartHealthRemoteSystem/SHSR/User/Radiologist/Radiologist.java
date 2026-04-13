package com.SmartHealthRemoteSystem.SHSR.User.Radiologist;

import org.springframework.data.mongodb.core.mapping.Document;
import com.SmartHealthRemoteSystem.SHSR.User.User;

@Document(collection = "Radiologist")
public class Radiologist extends User {

    private String department;
    private String specialization;
    private String profilePicture;
    private String profilePictureType;

    public Radiologist() {}

    public Radiologist(String userId, String name, String password, String contact,
                       String role, String email, String department, String specialization) {
        super(userId, name, password, contact, role, email);
        this.department = department;
        this.specialization = specialization;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getProfilePictureType() { return profilePictureType; }
    public void setProfilePictureType(String profilePictureType) { this.profilePictureType = profilePictureType; }
}
