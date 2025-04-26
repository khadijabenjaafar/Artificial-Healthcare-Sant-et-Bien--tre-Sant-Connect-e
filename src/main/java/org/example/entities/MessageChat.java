package org.example.entities;

import java.time.LocalDateTime;

public class MessageChat {
    private int id;
    private String contenu;
    private String type; // "UTILISATEUR" ou "BOT"
    private LocalDateTime dateHeure;

    public MessageChat(String contenu, String type, LocalDateTime dateHeure) {
        this.contenu = contenu;
        this.type = type;
        this.dateHeure = dateHeure;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public MessageChat(int id, String contenu, String type, LocalDateTime dateHeure) {
        this.id = id;
        this.contenu = contenu;
        this.type = type;
        this.dateHeure = dateHeure;
    }

    public MessageChat() {
    }

    @Override
    public String toString() {
        return "MessageChat{" +
                "id=" + id +
                ", contenu='" + contenu + '\'' +
                ", type='" + type + '\'' +
                ", dateHeure=" + dateHeure +
                '}';
    }
}
