package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.services.ServiceUtilisateur;
import org.example.entities.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class CreerCompte {
    @FXML
    private PasswordField mot_de_passe;

    @FXML
    private TextField Email;
    @FXML
    private TextField Adresse;
    @FXML
    private TextField telephone;
    @FXML
    private TextField nom;
    @FXML
    private TextField prenom;
    @FXML
    private DatePicker date_naissance;
    @FXML
    private RadioButton genreHomme;
    @FXML
    private RadioButton genreFemme;

    @FXML
    private ChoiceBox<String> role;
    @FXML
    private ImageView imageView;
    @FXML
    private Button importer;

    @FXML
    private Button inscription;
    public Utilisateur CurrentUser=UserConnecter.getInstance().getUserConnecter();

    private String imageUrl;

        private ServiceUtilisateur userService = new ServiceUtilisateur();

        @FXML
        public void initialize() {
            // Populate the ChoiceBox with roles
            role.setItems(FXCollections.observableArrayList("ROLE_FREELANCER", "ROLE_PATIENT"));
        }

    @FXML
    void Submit(ActionEvent event) throws SQLException, IOException {
        // Collecter les données des champs de saisie
        String nom1 = nom.getText();
        String prenom1 = prenom.getText();
        String email = Email.getText();
        String password = mot_de_passe.getText();
        String phoneNumber = telephone.getText();
        LocalDate date = date_naissance.getValue();
        String role1 = role.getValue();
        String adresse1 = Adresse.getText();
        String genre1 = null;

        // Vérification du genre sélectionné
        if (genreHomme.isSelected()) {
            genre1 = "Homme";
        } else if (genreFemme.isSelected()) {
            genre1 = "Femme";
        }

        // Validation des champs
        if (nom1.isEmpty() || password.isEmpty() || prenom1.isEmpty() || date == null || email.isEmpty() || adresse1.isEmpty() || phoneNumber.isEmpty() || role1 == null || genre1 == null) {
            showAlert("Error", "Tous les champs doivent être remplis");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Error", "Le format de l'email est invalide");
            return;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            showAlert("Error", "Le numéro de téléphone est invalide !");
            return;
        }

        if (!isStrongPassword(password))
        {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères, avec des lettres majuscules et minuscules.");

        }
        Utilisateur newUser = new Utilisateur(nom1, prenom1, email, password, date, EnumRole.valueOf(role1), adresse1, genre1, phoneNumber, imageUrl);

        userService.ajouter(newUser);
        UserConnecter.getInstance().setUserConnecter(newUser);


        showAlert("Success", "L'utilisateur a été créé avec succès");

        // Redirection vers indexFront.fxml
        try {
            // Charger le fichier fxml de la page d'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/indexFront.fxml"));
            Parent root = loader.load();

            // Si tu veux passer l'utilisateur au controller de indexFront, fais-le ici (optionnel)
            // IndexFrontController controller = loader.getController();
            // controller.setUser(CurrentUser); // Par exemple, tu peux ajouter cette méthode dans le controller de indexFront

            // Récupérer la scène actuelle et changer de scène
            Stage stage = (Stage) nom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page d'accueil.");
        }
    }


        private boolean isValidEmail(String email) {
            // Simple email validation regex
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            return email.matches(emailRegex);
        }

        private boolean isValidPhoneNumber(String phoneNumber) {
            // Simple phone number validation regex (adjust based on your requirements)
            String phoneRegex = "^[0-9]{8}$"; // Assumes a 10-digit phone number
            return phoneNumber.matches(phoneRegex);
        }

    private boolean isStrongPassword(String password) {
        // Mot de passe avec au moins 8 caractères, comprenant des majuscules et des minuscules
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*[a-z])(?=.*[A-Z])[A-Za-z]{8,}$";
        return password.matches(passwordRegex);
    }

        private void showAlert(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        private void clearForm() {
            nom.clear();
            mot_de_passe.clear();
            prenom.clear();
            Email.clear();
            Adresse.clear();
            telephone.clear();
            role.getSelectionModel().clearSelection();
            date_naissance.cancelEdit();
        }
        private void showAlert(Alert.AlertType alertType, String title, String message) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    @FXML
    private void importer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String imageUrl1 = file.toURI().toString();
            Image image = new Image(imageUrl1);
            imageView.setImage(image);
            imageUrl=imageUrl1;
        } else {
            // Tu peux afficher une alerte si tu veux :
            showAlert(Alert.AlertType.WARNING, "Aucune image sélectionnée", "Veuillez choisir une image.");
        }
    }


}
