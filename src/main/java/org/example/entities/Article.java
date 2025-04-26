package org.example.entities;

import java.sql.Date;

public class Article {
    private int id;
    private String titre;
    private Utilisateur utilisateur;
    private String contenue;
    private Date dateArticle;
    private String urlimagearticle;
    private int nbreVue;


    public Article(){}

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Article(int id, Utilisateur utilisateur, String titre, String contenue, Date dateArticle, String urlimagearticle, int nbreVue) {
        this.id = id;
        this.titre=titre;
        this.utilisateur = utilisateur;
        this.contenue = contenue;
        this.dateArticle = dateArticle;
        this.urlimagearticle = urlimagearticle;
        this.nbreVue = nbreVue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getContenue() {
        return contenue;
    }

    public void setContenue(String contenue) {
        this.contenue = contenue;
    }

    public Date getDateArticle() {
        return dateArticle;
    }

    public void setDateArticle(Date dateArticle) {
        this.dateArticle = dateArticle;
    }

    public String getUrlimagearticle() {
        return urlimagearticle;
    }

    public void setUrlimagearticle(String urlimagearticle) {
        this.urlimagearticle = urlimagearticle;
    }

    public int getNbreVue() {
        return nbreVue;
    }

    public void setNbreVue(int nbreVue) {
        this.nbreVue = nbreVue;
    }
}
