package org.example.test;

import org.example.entities.Consultation;
import org.example.entities.Ordonnance;
import org.example.services.ServiceOrdonnance;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialiser les services

        /*ServiceOrdonnance serviceOrdonnance = new ServiceOrdonnance();

        try {
            // Créer une nouvelle consultation
            Consultation consultation = new Consultation();
            consultation.setDiagnostic("Mal de tête");
            consultation.setTraitement("Paracétamol");
            consultation.setObservation("A prendre 3 fois par jour");
            consultation.setPrix("50");
            consultation.setProchainRdv(LocalDate.of(2025, 5, 10));
            consultation.setDuree("30 min");



            // Créer une nouvelle ordonnance
            Ordonnance ordonnance = new Ordonnance();
            ordonnance.setDate(LocalDate.of(2025, 4, 12));
            ordonnance.setMedicaments("Paracétamol");
            ordonnance.setCommantaire("A prendre après les repas");
            ordonnance.setDureeUtilisation("7 jours");
            ordonnance.setQuantiteUtilisation("2 comprimés par jour");


            // Ajouter l'ordonnance à la base de données
            serviceOrdonnance.ajouter(ordonnance);

            System.out.println("Ordonnance ajoutée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
        // Créer une instance du service Ordonnance
        /*ServiceOrdonnance serviceOrdonnance = new ServiceOrdonnance();

        try {
            // Afficher la liste des ordonnances
            List<Ordonnance> ordonnances = serviceOrdonnance.afficher();

            // Vérifier si la liste est vide
            if (ordonnances.isEmpty()) {
                System.out.println("Aucune ordonnance trouvée.");
            } else {
                // Afficher les informations de chaque ordonnance
                for (Ordonnance ordonnance : ordonnances) {
                    System.out.println("ID: " + ordonnance.getId());
                    System.out.println("Médicaments: " + ordonnance.getMedicaments());
                    System.out.println("Commentaire: " + ordonnance.getCommantaire());
                    System.out.println("Durée d'utilisation: " + ordonnance.getDureeUtilisation());
                    System.out.println("Quantité d'utilisation: " + ordonnance.getQuantiteUtilisation());
                    System.out.println("-----------------------------");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'affichage des ordonnances.");
        }
    }*/
        // Créer une instance du service Ordonnance
        ServiceOrdonnance serviceOrdonnance = new ServiceOrdonnance();

        // Scanner pour saisir les nouvelles informations
        Scanner scanner = new Scanner(System.in);

        try {
            // Demander l'ID de l'ordonnance à modifier
            System.out.print("Entrez l'ID de l'ordonnance à modifier : ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Consomme la nouvelle ligne après l'ID

            // Demander les nouvelles informations
            System.out.print("Entrez les nouveaux médicaments : ");
            String medicaments = scanner.nextLine();

            System.out.print("Entrez un nouveau commentaire : ");
            String commentaire = scanner.nextLine();

            System.out.print("Entrez la nouvelle durée d'utilisation : ");
            String dureeUtilisation = scanner.nextLine();

            System.out.print("Entrez la nouvelle quantité d'utilisation : ");
            String quantiteUtilisation = scanner.nextLine();

            // Créer une nouvelle ordonnance avec les informations modifiées
            Ordonnance ordonnance = new Ordonnance();
            ordonnance.setId(id);
            ordonnance.setMedicaments(medicaments);
            ordonnance.setCommantaire(commentaire);
            ordonnance.setDureeUtilisation(dureeUtilisation);
            ordonnance.setQuantiteUtilisation(quantiteUtilisation);

            // Appeler la méthode pour modifier l'ordonnance dans la base de données
            serviceOrdonnance.modifier(ordonnance);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la modification de l'ordonnance.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la saisie des données.");
        } finally {
            // Fermer le scanner
            scanner.close();
        }
    }
}
