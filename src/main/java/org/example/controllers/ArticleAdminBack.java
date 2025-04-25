package org.example.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.entities.Article;
import org.example.entities.Consultation;
import org.example.services.ServiceArticle;
import org.example.services.ServiceConsultation;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ArticleAdminBack {



    @FXML
    private VBox rootVBox;

    @FXML
    private GridPane grid;

    private final ServiceArticle service = new ServiceArticle();

    @FXML
    public void initialize() {
        try {
            List<Article> articles = service.afficher();
            int column = 0;
            int row = 0;

            for (Article c : articles) {
                VBox card = createCard(c);
                grid.add(card, column, row);

                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createCard(Article c) {
        // Créer la carte avec un espacement et une taille fixe
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #dddddd; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4)");

        // Fixer la taille de la carte
        card.setPrefWidth(250);  // Largeur fixe
        card.setPrefHeight(350); // Hauteur fixe (ajuster en fonction du contenu)

        // Créer et styliser les labels
        Label labelTitle = new Label("Title: " + c.getTitre());
        labelTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label labelObservation = new Label("Contenu: " + c.getContenue());

        // Créer l'image
        Image image = new Image(c.getUrlimagearticle()); // Charger l'image depuis l'URL
        ImageView imageView = new ImageView(image);

        // Fixer la taille de l'image
        imageView.setFitWidth(200);  // Largeur de l'image
        imageView.setFitHeight(120); // Hauteur de l'image
        imageView.setPreserveRatio(true);  // Maintenir le ratio d'aspect

        // Créer le label pour la date
        Label labelDate = new Label("Date: " + c.getDateArticle().toString());

        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(labelTitle, labelObservation, imageView, labelDate);

        return card;
    }

}
