package org.example.entities;

public class Notification {
    private Long id;
    private String message;
    private boolean isRead;
    private Utilisateur receiver;

    public Notification() {
    }

    public Notification(Long id, String message, boolean isRead, Utilisateur receiver) {
        this.id = id;
        this.message = message;
        this.isRead = isRead;
        this.receiver = receiver;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Utilisateur getReceiver() {
        return receiver;
    }

    public void setReceiver(Utilisateur receiver) {
        this.receiver = receiver;
    }

    public void setIsRead(boolean b) {
    }
}
