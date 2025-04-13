package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.entities.Facturation;
import org.example.services.ServiceFacturation;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AfficheFacturation implements Initializable {

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
        hbox.setSpacing(20);
        hbox.setPadding(new Insets(20));
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
        Label montantLabel = new Label("Montant : " + facturation.getMontant());
        montantLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label dateLabel = new Label("Date : " + facturation.getDateFacturation().toString());
        dateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label methodePLabel = new Label("Méthode : " + facturation.getMethodePaiement());
        methodePLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label statutLabel = new Label("Statut : " + facturation.getStatut());
        statutLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button viewDetailsBtn = new Button("Voir Détails");
        viewDetailsBtn.setStyle("-fx-background-color: #0fb5a7; -fx-text-fill: white;");
        viewDetailsBtn.setOnAction(e -> showDetailsModal(facturation));

        VBox card = new VBox(10, montantLabel, dateLabel, methodePLabel, statutLabel, viewDetailsBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-border-color: #15d5bc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 0);");

        return card;
    }


    private void showDetailsModal(Facturation facturation) {
        Label montant = new Label("Montant : " + facturation.getMontant());
        Label dateFacturation = new Label("Date de Facturation : " +
                (facturation.getDateFacturation() != null ? facturation.getDateFacturation().toString() : "Non défini"));

        Label statusFacturation = new Label("Statut de Facturation : " +
                (facturation.getStatut() != null ? facturation.getStatut().toString() : "Non défini"));

        Label methodeFacturation = new Label("Méthode de Paiement : " +
                (facturation.getMethodePaiement() != null ? facturation.getMethodePaiement().toString() : "Non défini"));

        Button modifierBtn = new Button("Modifier");
        modifierBtn.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white;");
        modifierBtn.setOnAction(event -> showModificationForm(facturation));

        Button supprimerBtn = new Button("Supprimer");
        supprimerBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        supprimerBtn.setOnAction(event -> {
            Stage confirmationStage = new Stage();
            confirmationStage.initModality(Modality.APPLICATION_MODAL);
            confirmationStage.setTitle("Confirmation de Suppression");

            Label confirmationLabel = new Label("Êtes-vous sûr de vouloir supprimer cette facturation ?");
            confirmationLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            Button yesButton = new Button("Oui");
            yesButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
            yesButton.setOnAction(e -> {
                try {
                    ServiceFacturation service = new ServiceFacturation();
                    service.supprimer(facturation.getId());
                    System.out.println("Facturation supprimée !");
                    confirmationStage.close();
                    modalStage.close();
                    initialize(null, null);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            Button noButton = new Button("Non");
            noButton.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white;");
            noButton.setOnAction(e -> confirmationStage.close());

            HBox buttonsBox = new HBox(10, yesButton, noButton);
            buttonsBox.setAlignment(Pos.CENTER);

            VBox confirmContent = new VBox(10, confirmationLabel, buttonsBox);
            confirmContent.setAlignment(Pos.CENTER_LEFT);
            confirmContent.setPadding(new Insets(20));

            Scene confirmScene = new Scene(confirmContent, 300, 150);
            confirmationStage.setScene(confirmScene);
            confirmationStage.showAndWait();
        });

        HBox buttonsBox = new HBox(10, modifierBtn, supprimerBtn);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox modalContent = new VBox(10, montant, dateFacturation, statusFacturation, methodeFacturation, buttonsBox);
        modalContent.setAlignment(Pos.CENTER_LEFT);
        modalContent.setPadding(new Insets(20));

        Scene modalScene = new Scene(modalContent, 400, 350);
        modalStage = new Stage();
        modalStage.setTitle("Détails de la Facturation");
        modalStage.setScene(modalScene);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.showAndWait();
    }


    private void showModificationForm(Facturation facturation) {
        Stage modificationStage = new Stage();
        modificationStage.initModality(Modality.APPLICATION_MODAL);
        modificationStage.setTitle("Modifier Facturation");
        System.out.println(facturation.getIdOrdonnanceIdId().getId());

        Label montantLabel = new Label("Montant : ");
        TextField montantField = new TextField(String.valueOf(facturation.getMontant()));

        Label methodeLabel = new Label("Méthode : ");
        TextField methodeField = new TextField(facturation.getMethodePaiement());

        Label statutLabel = new Label("Statut : ");
        TextField statutField = new TextField(facturation.getStatut());

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white;");
        saveBtn.setOnAction(event -> {
            String newMontant = montantField.getText();
            String newMethode = methodeField.getText();
            String newStatut = statutField.getText();

            boolean hasChanged =
                    !newMontant.equals(String.valueOf(facturation.getMontant())) ||
                            !newMethode.equals(facturation.getMethodePaiement()) ||
                            !newStatut.equals(facturation.getStatut());

            if (hasChanged) {
                try {
                    facturation.setMontant(Double.parseDouble(newMontant));
                    System.out.println(facturation.getMontant());
                    System.out.println(facturation.getIdOrdonnanceIdId().getId());
                    facturation.setMethodePaiement(newMethode);
                    facturation.setStatut(newStatut);

                    new ServiceFacturation().modifier(facturation);
                    System.out.println("Facturation mise à jour !");
                    modificationStage.close();
                    modalStage.close();
                    initialize(null, null);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        VBox vbox = new VBox(10, montantLabel, montantField, methodeLabel, methodeField, statutLabel, statutField, saveBtn);
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 400, 350);
        modificationStage.setScene(scene);
        modificationStage.showAndWait();
    }

}
