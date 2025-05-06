package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.entities.Utilisateur;
import org.example.entities.UserConnecter;
import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;


public class Profile {
    @FXML
    private TextField adresse;

    @FXML
    private DatePicker date;

    @FXML
    private TextField email;

    @FXML
    private TextField genre;

    @FXML
    private TextField nom;

    @FXML
    private TextField numtel;

    @FXML
    private TextField prenom;
    @FXML
    private ImageView profileImage;

    @FXML
    private Button ModifProf;
    @FXML
    private Button accueil;

    public Utilisateur CurrentUser=UserConnecter.getInstance().getUserConnecter();

    public void setDate(LocalDate date) {
        this.date.setValue(date);
    }


    public void setImage1(String imageView) {
        profileImage.setImage(new Image(imageView)); // adapte le chemin
    }


    public void setNom(String nom) {
        this.nom.setText(nom);
    }

    public void setPrenom(String prenom) {
        this.prenom.setText(prenom);
    }

    public void setAdresse(String adresse) {
        this.adresse.setText(adresse);
    }

    public void setEmail(String email) {
        this.email.setText(email);
    }
     public void setNumtel(String numtel) {
         this.numtel.setText(numtel);
     }

     public void setGenre(String genre) {
         this.genre.setText(genre);
    }

    @FXML
    private void NavigateToModifProfile(ActionEvent event) {

        try {
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/ModifProfile.fxml"));
            Parent root = loader1.load();

            ModifProfile modifProfileController = loader1.getController();

            // Passer les anciennes valeurs au contrôleur de la page de modification
            modifProfileController.setNom(CurrentUser.getNom());
            modifProfileController.setPrenom(CurrentUser.getPrenom());
            modifProfileController.setAdresse(CurrentUser.getAdresse());
            modifProfileController.setEmail(CurrentUser.getEmail());
            modifProfileController.setNumtel(CurrentUser.getnumTel());
            modifProfileController.setGenre(CurrentUser.getGenre());
            modifProfileController.setDate(CurrentUser.getDate_naissance());
            modifProfileController.setPassword(CurrentUser.getPassword());
            modifProfileController.setImage1(CurrentUser.getImage1());
            modifProfileController.setRole(CurrentUser.getRole());
            // Créer une nouvelle scène avec le contenu chargé et l'afficher
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void NavigateToFront() throws IOException {
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
