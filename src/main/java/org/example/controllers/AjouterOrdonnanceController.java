package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.entities.Ordonnance;
import org.example.services.ServiceOrdonnance;

import java.time.LocalDate;

public class AjouterOrdonnanceController {

    @FXML
    private TextField medicamentsField;
    @FXML
    private TextArea commentaireField;
    @FXML
    private TextField dureeUtilisationField;
    @FXML
    private TextField quantiteUtilisationField;
    @FXML
    private Button btnAjouter;

    @FXML
    private Label medicamentErrorLabel;
    @FXML
    private Label commentaireErrorLabel;
    @FXML
    private Label dureeErrorLabel;
    @FXML
    private Label quantiteErrorLabel;
    @FXML
    private Label dateErrorLabel;

    @FXML
    private DatePicker date;

    @FXML
    public void initialize() {
        // Initialisation si nécessaire
    }

    @FXML
    public void ajouterOrdonnance() {
        // Réinitialiser les labels d'erreur
        resetErrorLabels();

        try {
            // Récupérer les valeurs
            String medicaments = medicamentsField.getText().trim();
            String commentaire = commentaireField.getText().trim();
            String dureeUtilisation = dureeUtilisationField.getText().trim();
            String quantiteUtilisation = quantiteUtilisationField.getText().trim();
            LocalDate datePrescription = date.getValue();

            // Vérification des champs
            if (medicaments.isEmpty() || !isValidTextOrNumber(medicaments)) {
                medicamentErrorLabel.setText("Champ invalide pour 'Médicaments'.");
                medicamentErrorLabel.setVisible(true);
                throw new IllegalArgumentException("Champ invalide pour 'Médicaments'.");
            }

            if (commentaire.isEmpty() || !isValidTextOrNumber(commentaire)) {
                commentaireErrorLabel.setText("Champ invalide pour 'Commentaire'.");
                commentaireErrorLabel.setVisible(true);
                throw new IllegalArgumentException("Champ invalide pour 'Commentaire'.");
            }

            if (dureeUtilisation.isEmpty() || !isValidTextOrNumber(dureeUtilisation)) {
                dureeErrorLabel.setText("Champ invalide pour 'Durée d'utilisation'.");
                dureeErrorLabel.setVisible(true);
                throw new IllegalArgumentException("Champ invalide pour 'Durée d'utilisation'.");
            }

            if (quantiteUtilisation.isEmpty() || !isValidTextOrNumber(quantiteUtilisation)) {
                quantiteErrorLabel.setText("Champ invalide pour 'Quantité d'utilisation'.");
                quantiteErrorLabel.setVisible(true);
                throw new IllegalArgumentException("Champ invalide pour 'Quantité d'utilisation'.");
            }

            if (datePrescription == null) {
                dateErrorLabel.setText("La date est obligatoire.");
                dateErrorLabel.setVisible(true);
                throw new IllegalArgumentException("La date est obligatoire.");
            }

            // Créer l’ordonnance
            Ordonnance ordonnance = new Ordonnance();
            ordonnance.setMedicaments(medicaments);
            ordonnance.setCommantaire(commentaire);
            ordonnance.setDureeUtilisation(dureeUtilisation);
            ordonnance.setQuantiteUtilisation(quantiteUtilisation);
            ordonnance.setDate(datePrescription);

            // Ajouter avec le service
            ServiceOrdonnance service = new ServiceOrdonnance();
            service.ajouter(ordonnance);

            // Message succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Ordonnance ajoutée avec succès !");
            resetFields();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private boolean isValidTextOrNumber(String input) {
        // Autorise "3", "jours", "3 jours", "2 fois par jour", etc.
        return input.matches("[0-9]*\\s?[a-zA-ZÀ-ÿ\\s]*") && input.length() > 0;
    }

    private void resetErrorLabels() {
        medicamentErrorLabel.setVisible(false);
        commentaireErrorLabel.setVisible(false);
        dureeErrorLabel.setVisible(false);
        quantiteErrorLabel.setVisible(false);
        dateErrorLabel.setVisible(false);
    }

    private void resetFields() {
        medicamentsField.clear();
        commentaireField.clear();
        dureeUtilisationField.clear();
        quantiteUtilisationField.clear();
        date.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
