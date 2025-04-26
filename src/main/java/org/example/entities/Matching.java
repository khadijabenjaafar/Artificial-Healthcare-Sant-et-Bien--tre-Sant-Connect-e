
package org.example.entities;

import java.time.LocalDate;


public class Matching {

    private int id;

    private String cin;


    private String description;


    private LocalDate date;


    private String competences;


    private String cvPath;



    private Utilisateur idFreelancer;


    private Utilisateur utilisateur;


    private float price;


    private boolean availability = true;

    public Matching(int id, String competences, String description, double price, boolean availability, int utilisateurId) {

    }


    public String getCvFileName() {
        if (cvPath == null || cvPath.isEmpty()) {
            return "No CV";
        }
        return cvPath.substring(cvPath.lastIndexOf("/") + 1);
    }

    public Matching() {
        // Constructeur vide nécessaire pour pouvoir faire :
        // Matching m = new Matching();
    }

    public Matching(int id, String cin, String description, LocalDate date, String competences, String cvPath, float price, boolean availability) {
        this.id = id;
        this.cin = cin;
        this.description = description;
        this.date = date;
        this.competences = competences;
        this.cvPath = cvPath;
        this.idFreelancer = idFreelancer;
        this.utilisateur = utilisateur;
        this.price = price;
        this.availability = availability;

    }

    public int getId() {
        return id;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCompetences() {
        return competences;
    }

    public void setCompetences(String competences) {
        this.competences = competences;
    }

    public String getCvPath() {
        return cvPath;
    }

    public void setCvPath(String cvPath) {
        this.cvPath = cvPath;
    }

    public Utilisateur getIdFreelancer() {
        return idFreelancer;
    }

    public void setIdFreelancer(Utilisateur idFreelancer) {
        this.idFreelancer = idFreelancer;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public void setId(int id) {
        this.id = id;
    }

}
