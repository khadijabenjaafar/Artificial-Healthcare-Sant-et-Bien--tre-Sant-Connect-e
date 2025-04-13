package com.example.Controllers;

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
            Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/ajout-article.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMesArticles(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/mesarticles.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handlemodifArticles(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/avant-modif-article.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }










}
