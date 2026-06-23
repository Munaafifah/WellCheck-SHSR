package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing a physical imaging room/equipment unit.
 * Collection: RadiologyRoom
 *
 * Each modality (CT, MRI, XRAY, ULTRASOUND) has a fixed set of rooms.
 * Used by RoomController to determine which rooms are free for a given
 * date + time slot when a Radiographer schedules an appointment (UCR010).
 */
@Document(collection = "RadiologyRoom")
public class RadiologyRoom {

    @Id
    private String roomId;

    /** "CT", "MRI", "XRAY", or "ULTRASOUND" — must match the Modality enum names. */
    @Indexed
    private String modality;

    /** Display name, e.g. "CT Room 1". Also used as the `equipment` value on RadiologyAppointment. */
    private String roomName;

    /** Allows a room to be taken out of service without deleting the record. */
    private boolean isActive;

    public RadiologyRoom() {}

    public RadiologyRoom(String roomId, String modality, String roomName, boolean isActive) {
        this.roomId   = roomId;
        this.modality = modality;
        this.roomName = roomName;
        this.isActive = isActive;
    }

    // --- Getters ---
    public String  getRoomId()   { return roomId; }
    public String  getModality() { return modality; }
    public String  getRoomName() { return roomName; }
    public boolean isActive()    { return isActive; }

    // --- Setters ---
    public void setRoomId(String roomId)     { this.roomId = roomId; }
    public void setModality(String modality) { this.modality = modality; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public void setActive(boolean active)    { this.isActive = active; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String  roomId;
        private String  modality;
        private String  roomName;
        private boolean isActive = true;

        public Builder roomId(String v)     { this.roomId = v;   return this; }
        public Builder modality(String v)   { this.modality = v; return this; }
        public Builder roomName(String v)   { this.roomName = v; return this; }
        public Builder isActive(boolean v)  { this.isActive = v; return this; }

        public RadiologyRoom build() {
            return new RadiologyRoom(roomId, modality, roomName, isActive);
        }
    }
}