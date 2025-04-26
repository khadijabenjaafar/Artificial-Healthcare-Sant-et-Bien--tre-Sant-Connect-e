
package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.entities.Facturation;
import org.example.services.ServiceFacturation;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AfficheFacturationBack implements Initializable {

    @FXML
    private ScrollPane scrollPane;
    private Stage modalStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Facturation> facturations = new ServiceFacturation().afficher();
            afficherCarousel(facturations);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherCarousel(List<Facturation> facturations) {
        HBox hbox = new HBox();
        hbox.setSpacing(25);
        hbox.setPadding(new Insets(30));
        hbox.setAlignment(Pos.CENTER_LEFT);

        for (Facturation facturation : facturations) {
            VBox card = createFacturationCard(facturation);
            hbox.getChildren().add(card);
        }

        scrollPane.setContent(hbox);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
    }

    private VBox createFacturationCard(Facturation facturation) {
        Label title = new Label("Facturation");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#0fb5a7"));

        Label montantLabel = new Label("💰 Montant : " + facturation.getMontant() + " TND");
        Label dateLabel = new Label("📅 Date : " + facturation.getDateFacturation());
        Label methodePLabel = new Label("💳 Méthode : " + facturation.getMethodePaiement());
        Label statutLabel = new Label("📌 Statut : " + facturation.getStatut());

        for (Label label : new Label[]{montantLabel, dateLabel, methodePLabel, statutLabel}) {
            label.setFont(Font.font("Arial", 13));
        }

        Button viewDetailsBtn = new Button("👁 Voir Détails");
        viewDetailsBtn.setStyle("-fx-background-color: #0fb5a7; -fx-text-fill: white; -fx-font-weight: bold;");
        viewDetailsBtn.setOnAction(e -> showDetailsModal(facturation));

        VBox card = new VBox(10, title, montantLabel, dateLabel, methodePLabel, statutLabel, viewDetailsBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setPrefWidth(250);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #15d5bc;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0.1, 0, 2);"
        );

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #f7fdfd;" +
                        "-fx-border-color: #0fb5a7;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0.2, 0, 4);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #15d5bc;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0.1, 0, 2);"
        ));

        return card;
    }

    private void showDetailsModal(Facturation facturation) {
        Label montant = new Label("Montant : " + facturation.getMontant());
        Label dateFacturation = new Label("Date de Facturation : " + facturation.getDateFacturation());
        Label statusFacturation = new Label("Statut de Facturation : " + facturation.getStatut());
        Label methodeFacturation = new Label("Méthode de Paiement : " + facturation.getMethodePaiement());

        for (Label label : new Label[]{montant, dateFacturation, statusFacturation, methodeFacturation}) {
            label.setFont(Font.font("Arial", 13));
        }

        Button modifierBtn = new Button("✏ Modifier");
        modifierBtn.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white;");
        modifierBtn.setOnAction(event -> showModificationForm(facturation)); // réutilise l'autre méthode qui modifie dans l’interface

        Button supprimerBtn = new Button("🗑 Supprimer");
        supprimerBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        supprimerBtn.setOnAction(event -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de Suppression");
            confirmationAlert.setHeaderText("Êtes-vous sûr ?");
            confirmationAlert.setContentText("Supprimer cette facturation ?");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        new ServiceFacturation().supprimer(facturation.getId());
                        initialize(null, null); // rafraîchir
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Succès");
                        successAlert.setHeaderText("✅ Suppression réussie !");
                        successAlert.setContentText("La facturation a été supprimée avec succès.");
                        successAlert.showAndWait();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });

        HBox buttonsBox = new HBox(15, modifierBtn, supprimerBtn);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox modalContent = new VBox(15, montant, dateFacturation, statusFacturation, methodeFacturation, buttonsBox);
        modalContent.setAlignment(Pos.CENTER_LEFT);
        modalContent.setPadding(new Insets(25));

        // ⬇️ Injection dans ton interface principale
        scrollPane.setContent(modalContent);
    }

    private void showModificationForm(Facturation facturation) {
        // Création du formulaire
        Label montantLabel = new Label("Montant : ");
        TextField montantField = new TextField(String.valueOf(facturation.getMontant()));

        Label methodeLabel = new Label("Méthode : ");
        TextField methodeField = new TextField(facturation.getMethodePaiement());

        Label statutLabel = new Label("Statut : ");
        TextField statutField = new TextField(facturation.getStatut());

        Button saveBtn = new Button("💾 Enregistrer");
        saveBtn.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
        saveBtn.setOnAction(event -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Tu es sûr ?");
            confirmationAlert.setContentText("Veux-tu vraiment modifier cette facturation ?");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        facturation.setMontant(Double.parseDouble(montantField.getText()));
                        facturation.setMethodePaiement(methodeField.getText());
                        facturation.setStatut(statutField.getText());

                        new ServiceFacturation().modifier(facturation);

                        // Message de succès
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Succès");
                        successAlert.setHeaderText("✅ Modification réussie !");
                        successAlert.setContentText("La facturation a été modifiée avec succès.");
                        successAlert.showAndWait();

                        // Réinitialiser le contenu de scrollPane
                        initialize(null, null);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (NumberFormatException e) {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Erreur de saisie");
                        errorAlert.setHeaderText("Montant invalide !");
                        errorAlert.setContentText("Veuillez entrer un nombre valide.");
                        errorAlert.showAndWait();
                    }
                }
            });
        });

        VBox formBox = new VBox(10, montantLabel, montantField, methodeLabel, methodeField, statutLabel, statutField, saveBtn);
        formBox.setPadding(new Insets(20));
        formBox.setAlignment(Pos.CENTER_LEFT);

        // ⬇️ Injection dans le ScrollPane
        scrollPane.setContent(formBox);
    }

}
