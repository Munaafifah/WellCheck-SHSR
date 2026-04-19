package com.SmartHealthRemoteSystem.SHSR.Communication.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

public class CreateChatRequest {

    @NotEmpty(message = "Participants list must not be empty")
    @Size(min = 2, message = "A chat requires at least 2 participants")
    private List<String> participants;

    @NotBlank(message = "createdBy must not be blank")
    private String createdBy;

    private String subject;

    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
}
