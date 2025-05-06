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

public class ModifProfile {
    public Utilisateur CurrentUser= UserConnecter.getInstance().getUserConnecter();

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
    private String url;

    private ServiceUtilisateur userService = new ServiceUtilisateur();
    private UserConnecter userConnecter=new UserConnecter();

    @FXML
    public void initialize() {
        // Populate the ChoiceBox with roles
        role.setItems(FXCollections.observableArrayList("ROLE_FREELANCER", "ROLE_PATIENT", "ROLE_MEDECIN","ROLE_PHARMACIEN","ROLE_ADMIN"));

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
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
            this.url= file.toURI().toString();
        }
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
        String adresse1 =Adresse.getText();

        String genre1;
        if (genreHomme.isSelected()) {
            genre1 = "Homme";
        } else {
            genre1 = "Femme";
        }

        // Validate input fields
        if (nom1.isEmpty() || password.isEmpty() || prenom1.isEmpty()  ||date==null|| email.isEmpty() || adresse1.isEmpty() || phoneNumber.isEmpty() || role1 == null ||(!genreFemme.isSelected() && !genreHomme.isSelected())) {
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


        // Create a new User object
        Utilisateur newUser = new Utilisateur(CurrentUser.getId(),nom1,prenom1,email, password,date, EnumRole.valueOf(role1),adresse1,genre1, phoneNumber,url, CurrentUser.getStatus());
        // Use UserService to create the user
        userService.modifier(newUser);
        UserConnecter.getInstance().setUserConnecter(newUser);


        // Show success message
        showAlert("Success", "l'utilisateur est modifier avec success.");

        listeV();
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





    public void setDate(LocalDate date) {
        this.date_naissance.setValue(date);
    }


    public void setImage1(String imageView1) {
        imageView.setImage(new Image(imageView1));
        url=imageView1;}

    public void setPassword(String password){this.mot_de_passe.setText(password);}

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

    public void setGenre(String genre) {
        if (genre == null) {
            System.out.println("Genre est null !");
            return;
        }

        if (genre.equals("Homme")) {
            genreHomme.setSelected(true); // Il faut SET la sélection, pas juste appeler isSelected()
        } else {
            genreFemme.setSelected(true);
        }
    }
    public void setRole(EnumRole role) {
        if (role != null && this.role.getItems().contains(role.toString())) {
            this.role.setValue(role.toString());
        }
    }
    @FXML
    public void listeV(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Newindex.fxml"));
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