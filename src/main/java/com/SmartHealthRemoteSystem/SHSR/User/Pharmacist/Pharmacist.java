
//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.User.Pharmacist;

import org.springframework.data.mongodb.core.mapping.Document;

import com.SmartHealthRemoteSystem.SHSR.User.User;

@Document(collection = "Pharmacist")
public class Pharmacist extends User {

    private String hospital;
    private String position;
    private String profilePicture;
    private String profilePictureType;


    public Pharmacist() {}

    public Pharmacist(String userId, String name, String password, String contact, String role, String email,
                      String hospital, String position) {
        super(userId, name, password, contact, role, email);
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

    public String getProfilePictureType() {
        return profilePictureType;
    }

    public void setProfilePictureType(String profilePictureType) {
        this.profilePictureType = profilePictureType;
    }

   
    
    
}

