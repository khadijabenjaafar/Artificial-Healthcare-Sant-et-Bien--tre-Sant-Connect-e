package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.example.entities.Facturation;
import org.example.entities.Ordonnance;
import org.example.services.ServiceFacturation;
import org.example.services.ServiceOrdonnance;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterFacturationController implements Initializable {

    @FXML
    private DatePicker dateFacture;
    @FXML
    private TextField tfMontant;
    @FXML
    private ComboBox<String> cbMethodePaiement;
    @FXML
    private ComboBox<String> cbStatut;
    @FXML
    private ComboBox<Ordonnance> ordonnanceComboBox;

    private final ServiceFacturation serviceFacturation = new ServiceFacturation();
    private final ServiceOrdonnance serviceOrdonnance = new ServiceOrdonnance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Ordonnance> ordonnances = serviceOrdonnance.afficher();
            ObservableList<Ordonnance> observableList = FXCollections.observableArrayList(ordonnances);
            ordonnanceComboBox.setItems(observableList);

            ordonnanceComboBox.setCellFactory(param -> new ListCell<Ordonnance>() {
                @Override
                protected void updateItem(Ordonnance item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null) ? null : String.valueOf(item.getId()));
                }
            });

            ordonnanceComboBox.setButtonCell(new ListCell<Ordonnance>() {
                @Override
                protected void updateItem(Ordonnance item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null) ? null : String.valueOf(item.getId()));
                }
            });
            dateFacture.setValue(LocalDate.now());

            // ✅ Ajout des options aux ComboBox
            cbMethodePaiement.setItems(FXCollections.observableArrayList("Carte", "Espèces"));

            // Set default value for status and make it non-editable
            cbStatut.setItems(FXCollections.observableArrayList("En attente", "Payé", "Annulé"));
            cbStatut.setValue("En attente");
            cbStatut.setDisable(true); // This makes the ComboBox non-editable

        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des ordonnances : " + e.getMessage());
        }
    }

    @FXML
    public void ajouterFacturation() {
        try {
            Ordonnance ordonnance = ordonnanceComboBox.getValue();
            LocalDate date = dateFacture.getValue();

            if (ordonnance == null) {
                showAlert("Erreur", "Veuillez sélectionner une ordonnance.");
                return;
            }

            if (date == null) {
                showAlert("Erreur", "Veuillez sélectionner une date.");
                return;
            }

            double montant = Double.parseDouble(tfMontant.getText().trim());
            String methode = cbMethodePaiement.getValue();
            String statut = "En attente";

            if (methode == null) {
                showAlert("Erreur", "Veuillez choisir une méthode de paiement.");
                return;
            }

            Facturation facturation = new Facturation(null, ordonnance, date, montant, methode, statut);
            serviceFacturation.ajouter(facturation);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Facturation ajoutée avec succès !");
            alert.showAndWait();

            // ✅ Charger la nouvelle page après succès
            FXMLLoader loader = new FXMLLoader(getClass().getResource("doctor.fxml"));
            Parent root = loader.load();
            ordonnanceComboBox.getScene().setRoot(root);

        } catch (NumberFormatException e) {
            showAlert("Erreur de format", "Veuillez entrer une valeur numérique valide pour le montant.");
        } catch (SQLException e) {
            showAlert("Erreur SQL", "Une erreur est survenue lors de l'ajout de la facturation.");
            e.printStackTrace();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page Mes Factures.");
            e.printStackTrace();
        }
    }


    @FXML
    private void annulerFormulaire() {
        dateFacture.setValue(null);
        tfMontant.clear();
        cbMethodePaiement.getSelectionModel().clearSelection();
        cbStatut.getSelectionModel().clearSelection();
        ordonnanceComboBox.getSelectionModel().clearSelection();

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
