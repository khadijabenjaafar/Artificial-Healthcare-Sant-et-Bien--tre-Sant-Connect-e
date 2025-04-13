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

    private String imageUrl;

        private ServiceUtilisateur userService = new ServiceUtilisateur();

        @FXML
        public void initialize() {
            // Populate the ChoiceBox with roles
            role.setItems(FXCollections.observableArrayList("ROLE_FREELANCER", "ROLE_PATIENT", "ROLE_MEDECIN","ROLE_PHARMACIEN","ROLE_ADMIN"));
        }

        @FXML
        void Submit(ActionEvent event) throws SQLException, IOException {
            // Collect data from input fields
            String nom1 = nom.getText();
            String prenom1 = prenom.getText();
            String email = Email.getText();
            String password = mot_de_passe.getText();
            String phoneNumber = telephone.getText();
            LocalDate date=date_naissance.getValue();
            String role1 = role.getValue();
            String adresse1 =   Adresse.getText();
            String genre1 = null;
            if (genreHomme.isSelected()) {
                genre1 = "Homme";
            } else if (genreFemme.isSelected()) {
                genre1 = "Femme";
            }

            // Validate input fields
            if (nom1.isEmpty() || password.isEmpty() || prenom1.isEmpty()  ||date==null|| email.isEmpty() || adresse1.isEmpty() || phoneNumber.isEmpty() || role1 == null ||genre1 == null) {
                showAlert("Error", "Tout les champs doivent etre remplie");
                return;
            }

            if (!isValidEmail(email)) {
                showAlert("Error", "le format de l'email est invalide");
                return;
            }

            if (!isValidPhoneNumber(phoneNumber)) {
                showAlert("Error", "le numero de telephone est invalide!");
                return;
            }

           /* if (!isStrongPassword(password)) {
                showAlert("Error", "Password must be at least 8 characters long and include a mix of letters, numbers, and special characters.");
                return;
            }*/
            // Create a new User object
            Utilisateur newUser = new Utilisateur(nom1,prenom1,email, password,date,EnumRole.valueOf(role1),adresse1,genre1, phoneNumber,imageUrl);

            // Use UserService to create the user
            userService.ajouter(newUser);

            // Show success message
            showAlert("Success", "L'utilisateur est creer avec success");


            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
                Parent parent = loader.load();

                Profile profilControllers= loader.getController();
                profilControllers.setNom(nom.getText());
                profilControllers.setPrenom(prenom.getText());
                profilControllers.setAdresse(Adresse.getText());
                profilControllers.setEmail(Email.getText());
                profilControllers.setImage1(imageUrl);
                profilControllers.setGenre(genre1);
                profilControllers.setNumtel(phoneNumber);
                profilControllers.setDate(date_naissance.getValue());
                Stage stage = (Stage) prenom.getScene().getWindow();

                Scene scene = new Scene(parent);
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                System.out.println(e.getMessage());
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

       /* private boolean isStrongPassword(String password) {
            // Password strength validation: at least 8 characters, including letters, numbers, and special characters
            String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
            return password.matches(passwordRegex);
        }*/

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
