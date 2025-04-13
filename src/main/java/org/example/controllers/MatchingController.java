package org.example.controllers;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.entities.Matching;

import java.sql.*;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.entities.Utilisateur;
import org.example.entities.Planification;
import org.example.entities.Matching;
import org.example.services.ServiceUtilisateur;
import org.example.entities.EnumRole;
import org.example.services.ServicesPlanification;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class MatchingController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Utilisateur> freelancers = ServiceUtilisateur.findFreelancers();
            afficherCarousel(freelancers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private ScrollPane scrollPane;



    private void afficherCarousel(List<Utilisateur> utilisateurs) {
        HBox hbox = new HBox();
        hbox.setSpacing(20);
        hbox.setPadding(new Insets(20));
        hbox.setAlignment(Pos.CENTER_LEFT);

        for (Utilisateur user : utilisateurs) {
            VBox card = createUserCard(user);
            hbox.getChildren().add(card);
        }

        scrollPane.setContent(hbox);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
    }

    private VBox createUserCard(Utilisateur user) {
        ImageView imageView = new ImageView();
        try {
            Image image = new Image("file:" + user.getImage1(), 100, 100, true, true);
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Image not found: " + user.getImage1());
        }

        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setClip(new Circle(50, 50, 50));

        Label nameLabel = new Label(user.getPrenom() + " " + user.getNom());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label roleLabel = new Label(user.getRole().name());
        roleLabel.setFont(Font.font("Arial", 12));
        roleLabel.setTextFill(Color.GRAY);

        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.setStyle("-fx-background-color: #0fb5a7; -fx-text-fill: white;");
        viewDetailsBtn.setOnAction(e -> showDetailsModal(user)); // On appelle une méthode qu’on va créer


        VBox card = new VBox(10, imageView, nameLabel, roleLabel,viewDetailsBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setPrefWidth(150);
        viewDetailsBtn.setStyle("-fx-background-color: #11d5d5; -fx-text-fill: white; -fx-background-radius: 8;");
        card.setStyle("-fx-background-color: white; -fx-border-color: #15d5bc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 0);");


        return card;
    }
    private void showDetailsModal(Utilisateur freelancer) {
        // Image
        ImageView imageView = new ImageView();
        try {
            Image image = new Image("file:" + freelancer.getImage1(), 100, 100, true, true);
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Image not found: " + freelancer.getImage1());
        }
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setClip(new Circle(50, 50, 50));

        Label name = new Label(freelancer.getPrenom() + " " + freelancer.getNom());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        Label gender = new Label("Genre : " + freelancer.getGenre());
        gender.setFont(Font.font("Arial", 14));
        gender.setTextFill(Color.GRAY);

        Label specialty = new Label("Spécialité : " +
                (freelancer.getMatching() != null && freelancer.getMatching().getCompetences() != null
                        ? freelancer.getMatching().getCompetences() : "N/A"));

        // Bouton Demander une Consultation
        Button requestConsultationBtn = new Button("Demander une consultation");
        requestConsultationBtn.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;"
        );

        requestConsultationBtn.setOnAction(event -> openPlanificationForm(freelancer));

        VBox modalContent = new VBox(15, imageView, name, gender, specialty, requestConsultationBtn);
        modalContent.setAlignment(Pos.CENTER);
        modalContent.setPadding(new Insets(20));

        Scene modalScene = new Scene(modalContent, 400, 300);
        Stage modalStage = new Stage();
        modalStage.setTitle("Détails du Freelancer");
        modalStage.setScene(modalScene);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.showAndWait();
    }
    private void openPlanificationForm(Utilisateur freelancer) {
        // Champs de saisie
        DatePicker datePicker = new DatePicker();
        TextField adresseField = new TextField();
        adresseField.setPromptText("Adresse de consultation");

        ComboBox<String> modeComboBox = new ComboBox<>();
        modeComboBox.getItems().addAll("Présentiel", "À distance");
        modeComboBox.setPromptText("Mode de consultation");

        Button submitBtn = new Button("Envoyer la demande");
        submitBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");

        submitBtn.setOnAction(event -> {
            if (datePicker.getValue() == null || adresseField.getText().isEmpty() || modeComboBox.getValue() == null) {
                new Alert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs.").showAndWait();
            } else {
                // Créer une planification
                Planification planification = new Planification();
                planification.setDate(datePicker.getValue());
                planification.setAdresse(adresseField.getText());
                planification.setMode(modeComboBox.getValue());
                planification.setStatut("en attente");
                planification.setReponse(null);
                planification.setFreelancer(freelancer);
/*
                // Ici tu dois avoir l'utilisateur connecté
                Utilisateur currentUser = ServiceUtilisateur.getConnectedUser();  // Cette méthode tu dois l’implémenter !
                planification.setUtilisateur(currentUser);
*/
                if (planification.getUtilisateur() != null) {
                    int idUtilisateur = planification.getUtilisateur().getId();
                    System.out.println("L'utilisateur ID est : " + idUtilisateur);
                    // continue le traitement ici
                } else {
                    System.out.println("⚠️ Aucun utilisateur assigné à cette planification.");
                    // ici tu peux soit ignorer, soit mettre un utilisateur par défaut pour les tests :
                    Utilisateur fakeUser = new Utilisateur();
                    fakeUser.setId(1); // ID fictif pour test
                    planification.setUtilisateur(fakeUser);
                }
                ServicesPlanification service = new ServicesPlanification();
                try {
                    service.add(planification);
                    new Alert(Alert.AlertType.INFORMATION, "Demande envoyée avec succès !").showAndWait();
                } catch (SQLException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement !").showAndWait();
                }
            }
        });

        VBox form = new VBox(15,
                new Label("Choisissez la date :"), datePicker,
                new Label("Adresse :"), adresseField,
                new Label("Mode :"), modeComboBox,
                submitBtn
        );
        form.setPadding(new Insets(20));
        form.setAlignment(Pos.CENTER);

        Scene formScene = new Scene(form, 400, 350);
        Stage formStage = new Stage();
        formStage.setTitle("Demande de Consultation");
        formStage.setScene(formScene);
        formStage.initModality(Modality.APPLICATION_MODAL);
        formStage.showAndWait();
    }
    @FXML private TextField txtTitre;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private ComboBox<Utilisateur> comboUtilisateur;

    // Méthode appelée quand tu cliques sur le bouton "Valider"
    @FXML
    private void onBtnValiderClicked(ActionEvent event) {
        if (validerChamps()) {
            // ici tu enregistres la planification si tout est OK
            enregistrerPlanification();
        }
    }

    // Tu ajoutes cette méthode dans la même classe
    private boolean validerChamps() {
        StringBuilder erreurs = new StringBuilder();

        if (txtTitre.getText() == null || txtTitre.getText().trim().isEmpty()) {
            erreurs.append("❌ Le titre est obligatoire.\n");
        }

        if (dateDebut.getValue() == null) {
            erreurs.append("❌ La date de début est obligatoire.\n");
        }

        if (dateFin.getValue() == null) {
            erreurs.append("❌ La date de fin est obligatoire.\n");
        }

        if (comboUtilisateur.getValue() == null) {
            erreurs.append("❌ Veuillez sélectionner un utilisateur.\n");
        }

        if (dateDebut.getValue() != null && dateFin.getValue() != null &&
                dateDebut.getValue().isAfter(dateFin.getValue())) {
            erreurs.append("❌ La date de début ne peut pas être après la date de fin.\n");
        }

        if (erreurs.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Champs invalides");
            alert.setContentText(erreurs.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    // Exemple méthode pour enregistrer
    private void enregistrerPlanification() {
        // traitement d'enregistrement ici
    }

    @FXML
    private void handleAfficherMatching(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MatchingView.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste des Matchings");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleAfficherPlanification(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/PlanificationView.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste des Planifications");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }


}





