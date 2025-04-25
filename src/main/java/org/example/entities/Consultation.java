package org.example.entities;

import java.time.LocalDate;

public class Consultation {
    private int id;
    private String diagnostic;
    private String traitement;
    private String observation;
    private String prix;
    private LocalDate prochainRdv;
    private String duree;
    private RendezVous rendezVous;


    public Consultation(int id) {
        this.id = id;
    }

    public Consultation(LocalDate prochainRdv) {
        this.prochainRdv = prochainRdv;
    }

    public Consultation(int id, String diagnostic, String traitement, String observation, String prix, LocalDate prochainRdv, String duree, RendezVous rendezVous) {

        this.id = id;
        this.diagnostic = diagnostic;
        this.traitement = traitement;
        this.observation = observation;
        this.prix = prix;
        this.prochainRdv = prochainRdv;
        this.duree = duree;
        this.rendezVous = rendezVous;
    }
    public Consultation(int id, String diagnostic, String traitement, String observation, String prix, LocalDate prochainRdv, String duree) {
            this.id = id;
            this.diagnostic = diagnostic;
            this.traitement = traitement;
            this.observation = observation;
            this.prix = prix;
            this.prochainRdv = prochainRdv;
            this.duree = duree;
            this.rendezVous = rendezVous;
        }


    public Consultation(String diagnostic, String traitement, String observation, String prix, LocalDate prochainRdv, String duree, RendezVous rendezVous) {
        this.diagnostic = diagnostic;
        this.traitement = traitement;
        this.observation = observation;
        this.prix = prix;
        this.prochainRdv = prochainRdv;
        this.duree = duree;
        this.rendezVous = rendezVous;
    }
    public Consultation(String diagnostic, String traitement, String observation, String prix, LocalDate prochainRdv, String duree) {
        this.diagnostic = diagnostic;
        this.traitement = traitement;
        this.observation = observation;
        this.prix = prix;
        this.prochainRdv = prochainRdv;
        this.duree = duree;

    }

    public Consultation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getTraitement() {
        return traitement;
    }

    public void setTraitement(String traitement) {
        this.traitement = traitement;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public LocalDate getProchainRdv() {
        return prochainRdv;
    }

    public void setProchainRdv(LocalDate prochainRdv) {
        this.prochainRdv = prochainRdv;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public RendezVous getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(RendezVous rendezVous) {
        this.rendezVous = rendezVous;
    }


    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", diagnostic='" + diagnostic + '\'' +
                ", traitement='" + traitement + '\'' +
                ", observation='" + observation + '\'' +
                ", prix='" + prix + '\'' +
                ", prochainRdv=" + prochainRdv +
                ", duree='" + duree + '\'' +
                ", rendezVous=" + rendezVous +

                '}';
    }
}
