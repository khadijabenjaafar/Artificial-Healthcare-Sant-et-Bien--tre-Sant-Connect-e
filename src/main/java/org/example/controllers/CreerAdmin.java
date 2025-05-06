package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.api.PasswordUtils;
import org.example.entities.EnumRole;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import org.example.services.ServiceUtilisateur;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class CreerAdmin {
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


    @FXML private Label ErrorAdresse;
    @FXML private Label ErrorDate;
    @FXML private Label ErrorEmail;
    @FXML private Label ErrorGenre;
    @FXML private Label ErrorNom;
    @FXML private Label ErrorNumTel;
    @FXML private Label ErrorPassword;
    @FXML private Label ErrorPrenom;
    @FXML private Label ErrorRole;
    private ServiceUtilisateur userService = new ServiceUtilisateur();

    @FXML
    public void initialize() {
        // Populate the ChoiceBox with roles
        role.setItems(FXCollections.observableArrayList("ROLE_MEDECIN", "ROLE_ADMIN","ROLE_PHARMACIEN"));
    }
    private void resetErrorLabels() {
        ErrorDate.setVisible(false);
        ErrorAdresse.setVisible(false);
        ErrorEmail.setVisible(false);
        ErrorGenre.setVisible(false);
        ErrorNom.setVisible(false);
        ErrorNumTel.setVisible(false);
        ErrorPassword.setVisible(false);
        ErrorPrenom.setVisible(false);
    }
    @FXML
    void Submit(ActionEvent event) throws SQLException, IOException {
        resetErrorLabels();
        boolean hasError = false;
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

        if (role1 == null) {
            ErrorRole.setText("Le role est obligatoire.");
            ErrorRole.setVisible(true);
            hasError = true;
        }
        // Vérification du genre sélectionné
        if (genreHomme.isSelected()) {
            genre1 = "Homme";
        } else if (genreFemme.isSelected()) {
            genre1 = "Femme";
        }
        if (adresse1.isEmpty())
        {
            ErrorAdresse.setText("L'adresse est obligatoire.");
            ErrorAdresse.setVisible(true);
            hasError = true;
        }
        if (email.isEmpty())
        {
            ErrorEmail.setText("L'email obligatoire.");
            ErrorEmail.setVisible(true);
            hasError = true;
        }
        if (nom1.isEmpty())
        {
            ErrorNom.setText("Le nom est obligatoire.");
            ErrorNom.setVisible(true);
            hasError = true;
        }
        if (prenom1.isEmpty())
        {
            ErrorPrenom.setText("Le prenom est obligatoire.");
            ErrorPrenom.setVisible(true);
            hasError = true;
        }
        if (password.isEmpty())
        {
            ErrorPassword.setText("Le mot de passe est obligatoire.");
            ErrorPassword.setVisible(true);
            hasError = true;
        }
        if (date == null )
        {
            ErrorDate.setText("La date est obligatoire.");
            ErrorDate.setVisible(true);
            hasError = true;
        }
        if (phoneNumber.isEmpty())
        {
            ErrorNumTel.setText("Le numero est obligatoire.");
            ErrorNumTel.setVisible(true);
            hasError = true;
        }
        if (genre1== null)
        {
            ErrorGenre.setText("Le genre est obligatoire.");
            ErrorGenre.setVisible(true);
            hasError = true;
        }
        if (hasError) {
            return;
        }

        if (!isValidEmail(email)) {
            ErrorEmail.setText("L'email doit etre de la forme correcte (test@test.test).");
            ErrorEmail.setVisible(true);
            return;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            ErrorNumTel.setText("Le format du numéro de téléphone est invalide !");
            ErrorNumTel.setVisible(true);
            return;
        }

        String hashed = PasswordUtils.hashPassword(password);
        Utilisateur newUser;
        newUser = new Utilisateur(nom1, prenom1, email, hashed, date, EnumRole.valueOf(role1), adresse1, genre1, phoneNumber, imageUrl);


        userService.ajouter(newUser);

        showAlert("Success", "L'utilisateur a été créé avec succès");

        // Redirection vers indexFront.fxml
        try {
            // Charger le fichier fxml de la page d'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
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
    @FXML
    private void cancel(){
        try {
            // Charger le fichier fxml de la page d'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BackAdmin.fxml"));
            Parent root = loader.load();
            // Récupérer la scène actuelle et changer de scène
            Stage stage = (Stage) nom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page d'accueil.");
        }
    }


}