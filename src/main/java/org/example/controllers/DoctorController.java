package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DoctorController {
    @FXML
    private AnchorPane contentPane;



    @FXML
    private void handleAjouterArticle(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/ajout-article.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMesArticles(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/mesarticles.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handlemodifArticles(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/avant-modif-article.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handelajouterFacture(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/AjouterFacturation.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @FXML
    private void handleAjouterOrdonnance(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/AjoutOrdonnance.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleAfficherOrdonnances(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/AfficheOrdonnance.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleAfficherFacture(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/AfficheFacturation.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }













}
