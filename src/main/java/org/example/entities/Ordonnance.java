package org.example.entities;

import java.time.LocalDate;

public class Ordonnance {

    private int id;
    private LocalDate date;
    private String medicaments;
    private String commantaire;
    private String dureeUtilisation;
    private String quantiteUtilisation;
    private String texteQuantite; // Nouveau champ pour le texte de la quantité

    // Constructeur sans arguments
    public Ordonnance() {}

    // Constructeur avec tous les attributs
    public Ordonnance(Integer id, LocalDate date, String medicaments, String commantaire,
                      String dureeUtilisation, String quantiteUtilisation, String texteQuantite) {
        this.id = id;
        this.date = date;
        this.medicaments = medicaments;
        this.commantaire = commantaire;
        this.dureeUtilisation = dureeUtilisation;
        this.quantiteUtilisation = quantiteUtilisation;
        this.texteQuantite = texteQuantite; // Initialisation du texte de la quantité
    }

    // Constructeur avec id
    public Ordonnance(Integer id) {
        this.id = id;
    }

    // Getters et Setters pour chaque attribut

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMedicaments() {
        return medicaments;
    }

    public void setMedicaments(String medicaments) {
        this.medicaments = medicaments;
    }

    public String getCommantaire() {
        return commantaire;
    }

    public void setCommantaire(String commantaire) {
        this.commantaire = commantaire;
    }

    public String getDureeUtilisation() {
        return dureeUtilisation;
    }

    public void setDureeUtilisation(String dureeUtilisation) {
        this.dureeUtilisation = dureeUtilisation;
    }

    public String getQuantiteUtilisation() {
        return quantiteUtilisation;
    }

    public void setQuantiteUtilisation(String quantiteUtilisation) {
        this.quantiteUtilisation = quantiteUtilisation;
    }

    public String getTexteQuantite() {
        return texteQuantite;
    }

    public void setTexteQuantite(String texteQuantite) {
        this.texteQuantite = texteQuantite; // Setter pour texteQuantite
    }

    @Override
    public String toString() {
        return "Ordonnance{" +
                "id=" + id +
                ", date=" + date +
                ", medicaments='" + medicaments + '\'' +
                ", commantaire='" + commantaire + '\'' +
                ", dureeUtilisation='" + dureeUtilisation + '\'' +
                ", quantiteUtilisation='" + quantiteUtilisation + '\'' +
                ", texteQuantite='" + texteQuantite + '\'' + // Ajout du texteQuantite dans toString
                '}';
    }
}
