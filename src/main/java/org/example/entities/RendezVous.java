package org.example.entities;

import org.example.enums.Mode;
import org.example.enums.Motif;
import org.example.enums.Statut;

import java.time.LocalDateTime;

public class RendezVous {
    private int id;

    private LocalDateTime dateHeure;

    private Motif motif;

    private Statut statut;

    private Mode mode;

    private String commentaire;

    private Utilisateur medecin;

    private Utilisateur patient;

    public RendezVous(int id) {
        this.id = id;
    }

    public RendezVous(LocalDateTime dateHeure, Motif motif, Statut statut, Mode mode, String commentaire, Utilisateur medecin, Utilisateur patient) {
        this.dateHeure = dateHeure;
        this.motif = motif;
        this.statut = statut;
        this.mode = mode;
        this.commentaire = commentaire;
        this.medecin = medecin;
        this.patient = patient;
    }

    public RendezVous(int id, LocalDateTime dateHeure, Motif motif, Statut statut, Mode mode, String commentaire, Utilisateur medecin, Utilisateur patient) {
        this.id = id;
        this.dateHeure = dateHeure;
        this.motif = motif;
        this.statut = statut;
        this.mode = mode;
        this.commentaire = commentaire;
        this.medecin = medecin;
        this.patient = patient;
    }

    public RendezVous(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }
    public RendezVous() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {

        this.dateHeure = dateHeure;
    }

    public Motif getMotif() {
        return motif;
    }

    public void setMotif(Motif motif) {

        this.motif = motif;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {

        this.statut = statut;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {

        this.mode = mode;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {


        this.commentaire = commentaire;
    }

    public Utilisateur getMedecin() {
        return medecin;
    }

    public void setMedecin(Utilisateur medecin) {

        this.medecin = medecin;
    }

    public Utilisateur getPatient() {
        return patient;
    }

    public void setPatient(Utilisateur patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "RendezVous{" +
                "id=" + id +
                ", dateHeure=" + dateHeure +
                ", motif='" + motif + '\'' +
                ", statut='" + statut + '\'' +
                ", mode='" + mode + '\'' +
                ", commentaire='" + commentaire + '\'' +
                ", medecin=" + medecin +
                ", patient=" + patient +
                '}';
    }
}
