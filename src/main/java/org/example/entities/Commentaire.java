
package org.example.entities;

import java.sql.Date;
import java.sql.Time;


public class Commentaire {

    private int idCommentaire;
    private Utilisateur utilisateur;
    private Article article;
    private String contenue;
    private Date dateCommentaire;
    private boolean status;
    private Time heure;
    private int rating;
    private int likes;
    private int dislikes;
    private int parent_id ;


    public Commentaire(int idCommentaire, Utilisateur utilisateur, Article article, String contenue, Date dateCommentaire, boolean status, Time heure, Integer parentId) {
        this.idCommentaire = idCommentaire;
        this.utilisateur = utilisateur;
        this.article = article;
        this.contenue = contenue;
        this.dateCommentaire = dateCommentaire;
        this.status = status;
        this.heure = heure;
        this.parent_id = parentId;
    }

    public Commentaire(){}



    public int getParentId() {
        return parent_id;
    }

    // Setter
    public void setParentId(int parent_id) {
        this.parent_id = parent_id;
    }


    public int getIdCommentaire() {
        return idCommentaire;
    }

    public void setIdCommentaire(int idCommentaire) {
        this.idCommentaire = idCommentaire;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getContenue() {
        return contenue;
    }

    public void setContenue(String contenue) {
        this.contenue = contenue;
    }

    public Date getDateCommentaire() {
        return dateCommentaire;
    }

    public void setDateCommentaire(Date dateCommentaire) {
        this.dateCommentaire = dateCommentaire;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Time getHeure() {
        return heure;
    }

    public void setHeure(Time heure) {
        this.heure = heure;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}

