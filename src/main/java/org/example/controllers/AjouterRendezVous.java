package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.entities.RendezVous;
import org.example.entities.Utilisateur;
import org.example.enums.Mode;
import org.example.enums.Motif;
import org.example.enums.Statut;
import org.example.services.ServiceRendezVous;
import javafx.scene.input.MouseEvent;
import org.example.services.ServiceUtilisateur;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AjouterRendezVous {

    @FXML
    private DatePicker datePicker;
    @FXML
    private Spinner<Integer> spinnerHeure;
    @FXML
    private Spinner<Integer> spinnerMinute;
     @FXML
     private ComboBox<Motif> comboMotif;
     @FXML
     private ComboBox<Statut> comboStatut;
     @FXML
     private ComboBox<Mode> comboMode;
     @FXML
     private TextArea commentaireArea;
     @FXML
     private ComboBox<Utilisateur> comboMedecin;
     @FXML
     private ComboBox<Utilisateur> comboPatient;
     @FXML
     private Button btnAjouter;
    @FXML
    private VBox boxRendezVous;

    // Labels d'erreur
    @FXML private Label dateErrorLabel;
    @FXML private Label motifErrorLabel;
    @FXML private Label statutErrorLabel;
    @FXML private Label modeErrorLabel;
    @FXML private Label labelErrorCommentaire;
    @FXML private Label medecinErrorLabel;
    @FXML private Label patientErrorLabel;
@FXML
    public void initialize() {
    // Créez les valueFactory pour les spinners
    spinnerHeure.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
    spinnerMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        comboMotif.getItems().setAll(Motif.values());
        comboStatut.getItems().setAll(Statut.values());
        comboMode.getItems().setAll(Mode.values());
    // 🔒 Désactiver les dates passées
    datePicker.setDayCellFactory(picker -> new DateCell() {
        @Override
        public void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            setDisable(empty || date.isBefore(LocalDate.now()));
        }
    });
    try {
        // Remplir les ComboBox avec les utilisateurs (médecins et patients)
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
        List<Utilisateur> medecins = serviceUtilisateur.getMedecins();  // Récupérer les médecins
        List<Utilisateur> patients = serviceUtilisateur.getPatients();  // Récupérer les patients

        comboMedecin.getItems().setAll(medecins);
        comboPatient.getItems().setAll(patients);

        // Afficher les noms dans les ComboBox
        comboMedecin.setCellFactory(param -> new ListCell<Utilisateur>() {
            @Override
            protected void updateItem(Utilisateur item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNom() + " " + item.getPrenom());
            }
        });

        comboPatient.setCellFactory(param -> new ListCell<Utilisateur>() {
            @Override
            protected void updateItem(Utilisateur item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNom() + " " + item.getPrenom());
            }
        });
    } catch (Exception e) {
        e.printStackTrace();
    }

    }

    @FXML
    public void ajoutRendezVous(javafx.event.ActionEvent actionEvent) {
        resetErrorLabels();
        boolean hasError = false;
        StringBuilder messageErreur = new StringBuilder();

        try {
            if (datePicker.getValue() == null || spinnerHeure.getValue() == null || spinnerMinute.getValue() == null) {
                dateErrorLabel.setText("La date et l'heure sont obligatoires.");
                dateErrorLabel.setVisible(true);
                messageErreur.append("- La date et l'heure sont obligatoires.\n");
                hasError = true;
            } else {
                LocalDate date = datePicker.getValue();
                LocalTime time = LocalTime.of(spinnerHeure.getValue(), spinnerMinute.getValue());
                LocalDateTime dateHeure = LocalDateTime.of(date, time);

                if (dateHeure.isBefore(LocalDateTime.now())) {
                    dateErrorLabel.setText("La date et l'heure doivent être dans le futur.");
                    dateErrorLabel.setVisible(true);
                    messageErreur.append("- La date et l'heure doivent être dans le futur.\n");
                    hasError = true;
                }

                if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(16, 0))) {
                    dateErrorLabel.setText("L'heure doit être entre 08:00 et 16:00.");
                    dateErrorLabel.setVisible(true);
                    messageErreur.append("- L'heure doit être entre 08:00 et 16:00.\n");
                    hasError = true;
                }
            }

            Motif motif = comboMotif.getValue();
            if (motif == null) {
                motifErrorLabel.setText("Le motif est obligatoire.");
                motifErrorLabel.setVisible(true);
                messageErreur.append("- Le motif est obligatoire.\n");
                hasError = true;
            }

            Statut statut = comboStatut.getValue();
            if (statut == null) {
                statutErrorLabel.setText("Le statut est obligatoire.");
                statutErrorLabel.setVisible(true);
                messageErreur.append("- Le statut est obligatoire.\n");
                hasError = true;
            }

            Mode mode = comboMode.getValue();
            if (mode == null) {
                modeErrorLabel.setText("Le mode est obligatoire.");
                modeErrorLabel.setVisible(true);
                messageErreur.append("- Le mode est obligatoire.\n");
                hasError = true;
            }

            String commentaire = commentaireArea.getText();
            if (commentaire.isEmpty()) {
                labelErrorCommentaire.setText("Le commentaire ne peut pas être vide.");
                labelErrorCommentaire.setVisible(true);
                messageErreur.append("- Le commentaire ne peut pas être vide.\n");
                hasError = true;
            }

            Utilisateur medecin = comboMedecin.getValue();
            if (medecin == null) {
                medecinErrorLabel.setText("Veuillez sélectionner un médecin.");
                medecinErrorLabel.setVisible(true);
                messageErreur.append("- Veuillez sélectionner un médecin.\n");
                hasError = true;
            }

            Utilisateur patient = comboPatient.getValue();
            if (patient == null) {
                patientErrorLabel.setText("Veuillez sélectionner un patient.");
                patientErrorLabel.setVisible(true);
                messageErreur.append("- Veuillez sélectionner un patient.\n");
                hasError = true;
            }

            if (hasError) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", messageErreur.toString());
                return;
            }

            // Si tout est ok : créer et enregistrer le rendez-vous
            RendezVous rdv = new RendezVous(
                    LocalDateTime.of(datePicker.getValue(), LocalTime.of(spinnerHeure.getValue(), spinnerMinute.getValue())),
                    motif, statut, mode, commentaire, medecin, patient
            );

            ServiceRendezVous service = new ServiceRendezVous();
            service.ajouter(rdv);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Rendez-vous ajouté avec succès !");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }

    }


    // Méthode pour réinitialiser les labels d'erreur
    private void resetErrorLabels() {
        dateErrorLabel.setVisible(false);
        motifErrorLabel.setVisible(false);
        statutErrorLabel.setVisible(false);
        modeErrorLabel.setVisible(false);
        labelErrorCommentaire.setVisible(false);
        medecinErrorLabel.setVisible(false);
        patientErrorLabel.setVisible(false);
    }

    // Méthode pour afficher une alerte
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
