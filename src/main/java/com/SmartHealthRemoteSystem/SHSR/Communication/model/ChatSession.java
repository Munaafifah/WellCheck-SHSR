package com.SmartHealthRemoteSystem.SHSR.Communication.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "chat_sessions")
public class ChatSession {

    @Id
    private String chatId;
    private List<String> participants;
    private String createdBy;
    private ChatStatus status = ChatStatus.ACTIVE;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime closedAt;
    private String subject;

    public ChatSession() {}

    public ChatSession(String chatId, List<String> participants, String createdBy,
                       ChatStatus status, LocalDateTime createdAt,
                       LocalDateTime closedAt, String subject) {
        this.chatId = chatId;
        this.participants = participants;
        this.createdBy = createdBy;
        this.status = status;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.subject = subject;
    }

    // --- Getters ---

    public String getChatId()                { return chatId; }
    public List<String> getParticipants()    { return participants; }
    public String getCreatedBy()             { return createdBy; }
    public ChatStatus getStatus()            { return status; }
    public LocalDateTime getCreatedAt()      { return createdAt; }
    public LocalDateTime getClosedAt()       { return closedAt; }
    public String getSubject()               { return subject; }

    // --- Setters ---

    public void setChatId(String chatId)               { this.chatId = chatId; }
    public void setParticipants(List<String> p)        { this.participants = p; }
    public void setCreatedBy(String createdBy)         { this.createdBy = createdBy; }
    public void setStatus(ChatStatus status)           { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt = createdAt; }
    public void setClosedAt(LocalDateTime closedAt)    { this.closedAt = closedAt; }
    public void setSubject(String subject)             { this.subject = subject; }

    // --- Builder ---

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String chatId;
        private List<String> participants;
        private String createdBy;
        private ChatStatus status = ChatStatus.ACTIVE;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime closedAt;
        private String subject;

        public Builder chatId(String chatId)               { this.chatId = chatId; return this; }
        public Builder participants(List<String> p)        { this.participants = p; return this; }
        public Builder createdBy(String createdBy)         { this.createdBy = createdBy; return this; }
        public Builder status(ChatStatus status)           { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt)  { this.createdAt = createdAt; return this; }
        public Builder closedAt(LocalDateTime closedAt)    { this.closedAt = closedAt; return this; }
        public Builder subject(String subject)             { this.subject = subject; return this; }

        public ChatSession build() {
            return new ChatSession(chatId, participants, createdBy,
                    status, createdAt, closedAt, subject);
        }
    }
}
