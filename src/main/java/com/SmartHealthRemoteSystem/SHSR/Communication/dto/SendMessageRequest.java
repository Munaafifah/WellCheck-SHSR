package com.SmartHealthRemoteSystem.SHSR.Communication.dto;

import javax.validation.constraints.NotBlank;

public class SendMessageRequest {

    @NotBlank(message = "chatId must not be blank")
    private String chatId;

    @NotBlank(message = "senderId must not be blank")
    private String senderId;

    @NotBlank(message = "content must not be blank")
    private String content;

    private String imagingReferenceId;

    public String getChatId()               { return chatId; }
    public void setChatId(String chatId)    { this.chatId = chatId; }

    public String getSenderId()                  { return senderId; }
    public void setSenderId(String senderId)     { this.senderId = senderId; }

    public String getContent()                   { return content; }
    public void setContent(String content)       { this.content = content; }

    public String getImagingReferenceId()                         { return imagingReferenceId; }
    public void setImagingReferenceId(String imagingReferenceId)  { this.imagingReferenceId = imagingReferenceId; }
}
