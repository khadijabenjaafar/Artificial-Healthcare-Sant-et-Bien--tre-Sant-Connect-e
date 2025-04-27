package org.example.controllers;

import com.calendarfx.view.CalendarView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.entities.EnumRole;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import org.example.services.ServiceRendezVous;
import org.example.utils.NavigationUtil;


import java.io.IOException;
import java.sql.SQLException;


public class DoctorController {

    @FXML
    private Label nom;

    @FXML
    private Hyperlink css;

    @FXML
    private Hyperlink handleAfficherFacture;

    @FXML
    private Hyperlink handleAfficherOrdonnances;

    @FXML
    private Hyperlink handleAfficherPlanification;
    @FXML
    private Hyperlink handleAfficherMatching;

    @FXML
    private Hyperlink handlemodifArticles;
    @FXML
    private CalendarView calendarView;
    @FXML
    private ImageView photo;
    public Utilisateur CurrentUser=UserConnecter.getInstance().getUserConnecter();
    @FXML
    public void initialize() {
        if (CurrentUser != null) {
            if (nom != null) {
                nom.setText(CurrentUser.getNom());
            } else {
                System.err.println("Le label 'nom' est null !");
            }

            if (photo != null && CurrentUser.getImage1() != null) {
                try {
                    photo.setImage(new Image(CurrentUser.getImage1()));
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image : " + CurrentUser.getImage1());
                    e.printStackTrace();
                }
            } else {
                System.err.println("L'image 'photo' est null ou le chemin de l'image est null !");
            }
            if (CurrentUser.getRole()== EnumRole.ROLE_MEDECIN)
            {

                css.setVisible(true);
                handleAfficherOrdonnances.setVisible(true);
                handleAfficherFacture.setVisible(false);
                handlemodifArticles.setVisible(true);
                handleAfficherMatching.setVisible(false);
                handleAfficherPlanification.setVisible(false);
              //  handleMesArticles.setVisible(true);
            }
            else if (CurrentUser.getRole()== EnumRole.ROLE_PHARMACIEN)
            {
                css.setVisible(false);
                handleAfficherOrdonnances.setVisible(true);
                handleAfficherFacture.setVisible(true);
                handlemodifArticles.setVisible(true);
                handleAfficherMatching.setVisible(false);
                handleAfficherPlanification.setVisible(false);
              //  handleMesArticles.setVisible(true);
            }else {
                css.setVisible(false);
                handleAfficherOrdonnances.setVisible(false);
                handleAfficherFacture.setVisible(false);
                handlemodifArticles.setVisible(true);
                handleAfficherMatching.setVisible(true);
                handleAfficherPlanification.setVisible(true);
               // handleMesArticles.setVisible(true);
            }
        } else {
            System.err.println("Aucun utilisateur connecté.");
        }
    }



    @FXML
    private void handleAfficherMatching(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MatchingView.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleAfficherPlanification(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlanificationView.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void css(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardConsultation.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void handleAfficherCalendrier() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Calendrier.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    private void handlemodifArticles(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/avant-modif-article.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }






    @FXML
    private void handleAfficherOrdonnances(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheOrdonnance.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    private void handleAfficherFacture(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheFacturation.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void Deconnecter (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }




}
