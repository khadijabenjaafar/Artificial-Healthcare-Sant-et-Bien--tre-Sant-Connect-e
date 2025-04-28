package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.entities.RendezVous;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import org.example.enums.Mode;
import org.example.enums.Motif;
import org.example.enums.Statut;
import org.example.services.ServiceRendezVous;
import javafx.scene.input.MouseEvent;
import org.example.services.ServiceUtilisateur;

import java.io.IOException;
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

        comboMedecin.getItems().setAll(medecins);

        // Afficher les noms et prénoms dans le ComboBox des médecins
        comboMedecin.setCellFactory(param -> new ListCell<Utilisateur>() {
            @Override
            protected void updateItem(Utilisateur item, boolean empty) {
                super.updateItem(item, empty);
                // Afficher uniquement le nom et prénom
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " " + item.getPrenom());
                }
            }
        });

        // Afficher le nom et prénom du médecin sélectionné dans le bouton du ComboBox
        comboMedecin.setButtonCell(new ListCell<Utilisateur>() {
            @Override
            protected void updateItem(Utilisateur item, boolean empty) {
                super.updateItem(item, empty);
                // Afficher uniquement le nom et prénom
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom() + " " + item.getPrenom());
                }
            }
        });

    } catch (Exception e) {
        e.printStackTrace();
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

    @FXML
    public void ajoutRendezVous(javafx.event.ActionEvent actionEvent) {
        resetErrorLabels();
        boolean hasError = false;
        StringBuilder messageErreur = new StringBuilder();

        Utilisateur currentUser = UserConnecter.getInstance().getUserConnecter();

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

            if (hasError) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", messageErreur.toString());
                return;
            }

            // Création du rendez-vous avec le currentUser comme patient
            RendezVous rdv = new RendezVous(
                    LocalDateTime.of(datePicker.getValue(), LocalTime.of(spinnerHeure.getValue(), spinnerMinute.getValue())),
                    motif, statut, mode, commentaire, medecin, currentUser
            );

            ServiceRendezVous service = new ServiceRendezVous();
            service.ajouter(rdv);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Rendez-vous ajouté avec succès !");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardRendezVous.fxml"));
                Parent root = loader.load();

                // Récupérer la fenêtre actuelle et changer la scène
                Stage stage = (Stage) datePicker.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    public void annulerRendezVous(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardRendezVous.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) datePicker.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
