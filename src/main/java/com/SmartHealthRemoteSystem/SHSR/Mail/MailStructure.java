package com.SmartHealthRemoteSystem.SHSR.Mail;

public class MailStructure {
    private String to;
    private String password;
    private String subject;
    private String message;

    public MailStructure(String to, String password) {
        this.to = to;
        this.password = password;
    }

    public MailStructure(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
