package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.entities.Consultation;
import org.example.entities.RendezVous;
import org.example.services.ServiceConsultation;
import org.example.services.ServiceRendezVous;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AjouterConsultation {
    @FXML
    private TextField diagnosticField;

    @FXML
    private TextField traitementField;

    @FXML
    private TextArea observationArea;

    @FXML
    private TextField prixField;

    @FXML
    private DatePicker prochainRdvPicker;

    @FXML
    private TextField dureeField;

    @FXML
    private ComboBox<RendezVous> comboRendezVous;

    @FXML
    private Button btnAjouterConsultation;

    private final ServiceConsultation serviceConsultation = new ServiceConsultation();
    private final ServiceRendezVous serviceRendezVous = new ServiceRendezVous();

    @FXML
    public void initialize() {
        try {
            List<RendezVous> rendezVousList = serviceRendezVous.afficher();
            comboRendezVous.getItems().addAll(rendezVousList);

            // Modifier l'affichage du ComboBox pour n'afficher que la dateHeure
            comboRendezVous.setCellFactory(param -> new ListCell<RendezVous>() {
                @Override
                protected void updateItem(RendezVous item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Afficher uniquement la dateHeure du RendezVous
                        setText(item.getDateHeure().toString());
                    }
                }
            });

            // Afficher uniquement la dateHeure dans la sélection du ComboBox
            comboRendezVous.setButtonCell(new ListCell<RendezVous>() {
                @Override
                protected void updateItem(RendezVous item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getDateHeure().toString());
                    }
                }
            });

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement des rendez-vous : " + e.getMessage());
            e.printStackTrace(); // utile pour debug
        }

        prochainRdvPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now().plusDays(1))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // facultatif : couleur rouge clair
                }
            }
        });
    }



    @FXML
    private void ajouterConsultation() throws SQLException {
        String diagnostic = diagnosticField.getText();
        String traitement = traitementField.getText();
        String observation = observationArea.getText();
        String prixText = prixField.getText();
        LocalDate prochainRdv = prochainRdvPicker.getValue();
        String duree = dureeField.getText();
        RendezVous selectedRdv = comboRendezVous.getValue();

        // Validation de base
        if (diagnostic.isEmpty() || traitement.isEmpty() || prixText.isEmpty() || duree.isEmpty() || selectedRdv == null) {
            showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs obligatoires.");
            return;
        }


        try {
            Double.parseDouble(prixText); // Juste pour vérifier la validité
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Le prix doit être un nombre valide.");
            return;
        }
        try {
            int dureeInt = Integer.parseInt(duree);
            if (dureeInt <= 0) {
                showAlert(Alert.AlertType.ERROR, "La durée doit être un nombre entier strictement positif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "La durée doit être un nombre entier valide.");
            return;
        }



        Consultation consultation = new Consultation();
        consultation.setDiagnostic(diagnostic);
        consultation.setTraitement(traitement);
        consultation.setObservation(observation);
        consultation.setPrix(prixText);
        consultation.setProchainRdv(prochainRdv);
        consultation.setDuree(duree);
        consultation.setRendezVous(selectedRdv);

        serviceConsultation.ajouter(consultation);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Consultation ajoutée avec succès !");
        alert.showAndWait();  // <-- ATTEND que l'utilisateur ferme avant de continuer

        // Après la fermeture de l'alerte
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardConsultation.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) dureeField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        diagnosticField.clear();
        traitementField.clear();
        observationArea.clear();
        prixField.clear();
        prochainRdvPicker.setValue(null);
        dureeField.clear();
        comboRendezVous.setValue(null);
    }


}
