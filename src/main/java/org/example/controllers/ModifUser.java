
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
import org.example.entities.EnumRole;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import org.example.services.ServiceUtilisateur;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
public class ModifUser {


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
        private Button inscription;
    @FXML
    private ImageView imageView;


    private ServiceUtilisateur userService = new ServiceUtilisateur();

        @FXML
        public void initialize() {
            // Populate the ChoiceBox with roles
            role.setItems(FXCollections.observableArrayList("ROLE_FREELANCER", "ROLE_PATIENT", "ROLE_MEDECIN","ROLE_PHARMACIEN","ROLE_ADMIN"));
        }

        @FXML
        void Submit(ActionEvent event) throws SQLException {
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
            if (nom1.isEmpty() || password.isEmpty() || prenom1.isEmpty()  ||date==null|| email.isEmpty() || adresse1.isEmpty() || phoneNumber.isEmpty() || role1 == null ||genre1.isEmpty()) {
                showAlert("Error", "Tout les champs doivent etre remplie");
                return;
            }

            if (!isValidEmail(email)) {
                showAlert("Error", "Invalid email format.");
                return;
            }

            if (!isValidPhoneNumber(phoneNumber)) {
                showAlert("Error", "Invalid phone number format.");
                return;
            }

           /* if (!isStrongPassword(password)) {
                showAlert("Error", "Password must be at least 8 characters long and include a mix of letters, numbers, and special characters.");
                return;
            }*/
            String url=importer();

            // Create a new User object
            Utilisateur newUser = new Utilisateur(nom1,prenom1,email, password,date, EnumRole.valueOf(role1),adresse1,genre1, phoneNumber,url);

            // Use UserService to create the user
            userService.modifier(newUser);

            // Show success message
            showAlert("Success", "User created successfully.");

            //navigateToLogin();
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
    private String importer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
            return file.toURI().toString();
        }
        return null;

    }
    public void setDate(LocalDate date) {
        this.date_naissance.setValue(date);
    }


    public void setImage1(String imageView1) {
        imageView.setImage(new Image(imageView1)); // adapte le chemin
    }


    public void setNom(String nom) {
        this.nom.setText(nom);
    }

    public void setPrenom(String prenom) {
        this.prenom.setText(prenom);
    }

    public void setAdresse(String adresse) {
        this.Adresse.setText(adresse);
    }

    public void setEmail(String email) {
        this.Email.setText(email);
    }
    public void setNumtel(String numtel) {
        this.telephone.setText(numtel);
    }

    //public void setGenre(String genre) {this.genre.setText(genre);}
    @FXML
    public void listeV(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VoirUser.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) nom.getScene().getWindow(); // ou un autre bouton si ModifProf n'existe pas
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
