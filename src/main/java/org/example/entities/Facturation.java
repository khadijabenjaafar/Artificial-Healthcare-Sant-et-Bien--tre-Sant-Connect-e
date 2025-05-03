package org.example.entities;

import java.time.LocalDate;

public class Facturation {

    private int id;
    private Ordonnance idOrdonnanceIdId; // Correctement nommé en fonction de votre base
    private LocalDate dateFacturation;
    private double montant;
    private String methodePaiement;
    private String statut;

    // Constructeur avec les paramètres nécessaires
    public Facturation(Integer id ,Ordonnance idOrdonnanceIdId, LocalDate dateFacturation, double montant, String methodePaiement, String statut) {
        this.idOrdonnanceIdId = idOrdonnanceIdId;
        this.dateFacturation = dateFacturation;
        this.montant = montant;
        this.methodePaiement = methodePaiement;
        this.statut = statut;
    }

    public Facturation(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ordonnance getIdOrdonnanceIdId() {
        return idOrdonnanceIdId;
    }

    public void setIdOrdonnanceIdId(Ordonnance idOrdonnanceIdId) {
        this.idOrdonnanceIdId = idOrdonnanceIdId;
    }

    public LocalDate getDateFacturation() {
        return dateFacturation;
    }

    public void setDateFacturation(LocalDate dateFacturation) {
        this.dateFacturation = dateFacturation;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getMethodePaiement() {
        return methodePaiement;
    }

    public void setMethodePaiement(String methodePaiement) {
        this.methodePaiement = methodePaiement;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // Ajouter la méthode toString pour retourner une représentation de l'objet
    @Override
    public String toString() {
        return "Facturation ID: " + id + "\n" +
                "Montant: " + montant + " TND\n" +
                "Date: " + dateFacturation + "\n" +
                "Méthode de Paiement: " + methodePaiement + "\n" +
                "Statut: " + statut;
    }
}
