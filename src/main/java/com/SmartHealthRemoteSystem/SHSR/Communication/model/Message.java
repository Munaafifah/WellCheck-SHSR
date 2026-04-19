package com.SmartHealthRemoteSystem.SHSR.Communication.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
public class Message {

    @Id
    private String messageId;
    private String chatId;
    private String senderId;
    private String content;
    private String imagingReferenceId;
    private MessageStatus status = MessageStatus.SENT;
    private LocalDateTime timestamp = LocalDateTime.now();

    public Message() {}

    public Message(String messageId, String chatId, String senderId, String content,
                   String imagingReferenceId, MessageStatus status, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        this.imagingReferenceId = imagingReferenceId;
        this.status = status;
        this.timestamp = timestamp;
    }

    // --- Getters ---

    public String getMessageId()             { return messageId; }
    public String getChatId()                { return chatId; }
    public String getSenderId()              { return senderId; }
    public String getContent()               { return content; }
    public String getImagingReferenceId()    { return imagingReferenceId; }
    public MessageStatus getStatus()         { return status; }
    public LocalDateTime getTimestamp()      { return timestamp; }

    // --- Setters ---

    public void setMessageId(String messageId)                  { this.messageId = messageId; }
    public void setChatId(String chatId)                        { this.chatId = chatId; }
    public void setSenderId(String senderId)                    { this.senderId = senderId; }
    public void setContent(String content)                      { this.content = content; }
    public void setImagingReferenceId(String imagingReferenceId){ this.imagingReferenceId = imagingReferenceId; }
    public void setStatus(MessageStatus status)                 { this.status = status; }
    public void setTimestamp(LocalDateTime timestamp)           { this.timestamp = timestamp; }

    // --- Builder ---

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String messageId;
        private String chatId;
        private String senderId;
        private String content;
        private String imagingReferenceId;
        private MessageStatus status = MessageStatus.SENT;
        private LocalDateTime timestamp = LocalDateTime.now();

        public Builder messageId(String messageId)                   { this.messageId = messageId; return this; }
        public Builder chatId(String chatId)                         { this.chatId = chatId; return this; }
        public Builder senderId(String senderId)                     { this.senderId = senderId; return this; }
        public Builder content(String content)                       { this.content = content; return this; }
        public Builder imagingReferenceId(String imagingReferenceId) { this.imagingReferenceId = imagingReferenceId; return this; }
        public Builder status(MessageStatus status)                  { this.status = status; return this; }
        public Builder timestamp(LocalDateTime timestamp)            { this.timestamp = timestamp; return this; }

        public Message build() {
            return new Message(messageId, chatId, senderId, content,
                    imagingReferenceId, status, timestamp);
        }
    }
}
