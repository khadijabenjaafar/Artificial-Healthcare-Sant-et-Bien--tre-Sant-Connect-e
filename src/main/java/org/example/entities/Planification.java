package org.example.entities;

import java.time.LocalDate;


public class Planification {


    private Long id;


    private String statut = "en attente";


    private LocalDate date;


    private String adresse;

    private String reponse;


    private String mode;


    private Utilisateur freelancer;


    private Utilisateur utilisateur;

    public Planification() {
    }

    public Planification(Long id, String statut, LocalDate date, String adresse, String reponse, String mode, Utilisateur freelancer, Utilisateur utilisateur) {
        this.id = id;
        this.statut = statut;
        this.date = date;
        this.adresse = adresse;
        this.reponse = reponse;
        this.mode = mode;
        this.freelancer = freelancer;
        this.utilisateur = utilisateur;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Utilisateur getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(Utilisateur freelancer) {
        this.freelancer = freelancer;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

}
