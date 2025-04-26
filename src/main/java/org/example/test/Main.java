
package org.example.test;

import org.example.entities.Consultation;
import org.example.entities.RendezVous;
import org.example.entities.Utilisateur;
import org.example.enums.Mode;
import org.example.enums.Motif;
import org.example.enums.Statut;
import org.example.services.ServiceConsultation;
import org.example.services.ServiceRendezVous;
import org.example.utils.MyDataBase;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ServiceRendezVous srv = new ServiceRendezVous();
        ServiceConsultation sc= new ServiceConsultation();
        //RendezVous rdv = new RendezVous(31);
        /*Consultation consultation = new Consultation(
                " Infection virale",
                "Antibiotiques",
                " Suivi dans une semaine",
                "150",
                LocalDate.of(2025, 4, 15),
                "45",
                rdv
        );*/
        //Utilisateur patient = new Utilisateur(1);
        //Utilisateur medecin = new Utilisateur(2);

        /*RendezVous rendezVous = new RendezVous(
                LocalDateTime.of(2025, 4, 5, 10, 30),
                Motif.controle,          // Exemple de motif
                Statut.confirme,         // Exemple de statut
                Mode.EN_LIGNE,           // Exemple de mode
                "Consultation régulière",
                medecin,
                patient
        );*/

        try {
            //sc.supprimer(13);
            //System.out.println("Consultation supprimé !");
            //sc.ajouter(consultation);
            //System.out.println("Consultation ajouté avec succès !");

            /*Consultation consultationM = new Consultation(
                    12,
                    "Diagnostic mis à jour",
                    "Nouveau traitement",
                    "Observation mise à jour",
                    "200",
                    LocalDate.of(2025, 5, 10),
                    "60",
                    rdv);

            sc.modifier(consultationM);
            System.out.println("Consultation maj !");*/
            //srv.ajouter(rendezVous);
            //System.out.println("Rendez-vous ajouté avec succès !");

            /*RendezVous rendezVousToModify = new RendezVous(
                    16,  // ID du rendez-vous à modifier
                    LocalDateTime.of(2025, 4, 10, 15, 0), // Nouvelle date
                    Motif.SUIVI,  // Nouveau motif
                    Statut.CONFIRME,  // Nouveau statut
                    Mode.EN_LIGNE,  // Nouveau mode
                    "Rendez-vous modifié",
                    medecin,
                    patient
            );

            srv.modifier(rendezVousToModify); // Modification du rendez-vous
            System.out.println("Rendez-vous modifié avec succès !");*/


            //System.out.println(srv.afficher());
            /*RendezVous rdv = new RendezVous(
                    LocalDateTime.of(2025, 4, 5, 10, 30),
                    "Consultation",
                    "Confirmé",
                    "Physique",
                    "Consultation ",
                    medecin,
                    patient
            );
            srv.ajouter(rdv);
            System.out.println("Rendez-vous ajouté avec succès !");

            RendezVous rdvToModify = new RendezVous(
                    14,  // ID du rendez-vous à modifier
                    LocalDateTime.of(2025, 4, 10, 15, 0),
                    "Suivi médical",
                    "Reporté",
                    "Vidéo",
                    "Changement d'horaire",
                    medecin,
                    patient
            );
            srv.modifier(rdvToModify);
            System.out.println("Rendez-vous modifié avec succès !");

            srv.supprimer(21);
            System.out.println("Rendez-vous supprimé !");*/

            //List<RendezVous> rendezVousList = srv.afficher();
            /*for (RendezVous rdv : rendezVousList) {
                System.out.println("Rendez-vous ID: " + rdv.getId());
                System.out.println("Date et Heure: " + rdv.getDateHeure());
                System.out.println("Motif: " + rdv.getMotif());
                System.out.println("Statut: " + rdv.getStatut());
                System.out.println("Mode: " + rdv.getMode());
                System.out.println("Médecin: " + rdv.getMedecin().getNom() + " " + rdv.getMedecin().getPrenom());
                System.out.println("Patient: " + rdv.getPatient().getNom() + " " + rdv.getPatient().getPrenom());
                System.out.println("====================================");
            }*/

            List<Consultation> consultationList = sc.afficher();

            // Affichage des informations pour chaque consultation
            for (Consultation consultation : consultationList) {
                // Afficher les détails du rendez-vous associé
                RendezVous rdv = consultation.getRendezVous(); // Rendez-vous associé à la consultation
                System.out.println("Consultation ID: " + consultation.getId());
                System.out.println("Rendez-vous ID: " + rdv.getId());
                System.out.println("Date et Heure du Rendez-vous: " + rdv.getDateHeure());
                System.out.println("Diagnostic: " + consultation.getDiagnostic());
                System.out.println("Traitement: " + consultation.getTraitement());
                System.out.println("Observation: " + consultation.getObservation());
                System.out.println("Prix: " + consultation.getPrix());
                System.out.println("Prochain Rendez-vous: " + consultation.getProchainRdv());
                System.out.println("Durée: " + consultation.getDuree());
                System.out.println("====================================");
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }
}

