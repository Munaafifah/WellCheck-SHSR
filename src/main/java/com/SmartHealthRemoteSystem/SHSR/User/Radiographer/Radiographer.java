package com.SmartHealthRemoteSystem.SHSR.User.Radiographer;

import org.springframework.data.mongodb.core.mapping.Document;
import com.SmartHealthRemoteSystem.SHSR.User.User;

@Document(collection = "Radiographer")
public class Radiographer extends User {

    private String department;
    private String position;
    private String profilePicture;
    private String profilePictureType;

    public Radiographer() {}

    public Radiographer(String userId, String name, String password, String contact,
                        String role, String email, String department, String position) {
        super(userId, name, password, contact, role, email);
        this.department = department;
        this.position = position;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getProfilePictureType() { return profilePictureType; }
    public void setProfilePictureType(String profilePictureType) { this.profilePictureType = profilePictureType; }
}
