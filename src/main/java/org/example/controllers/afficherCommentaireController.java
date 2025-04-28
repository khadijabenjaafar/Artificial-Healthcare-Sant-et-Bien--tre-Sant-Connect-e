package org.example.controllers;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.entities.Article;
import org.example.entities.Commentaire;
import org.example.services.ServiceArticle;
import org.example.services.ServiceCommentaire;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class afficherCommentaireController {


    @FXML
    private VBox articlesContainer;

    private final ServiceArticle serviceArticle = new ServiceArticle();
    private final ServiceCommentaire serviceCommentaire = new ServiceCommentaire();

    @FXML
    public void initialize() {
        try {
            List<Article> articles = serviceArticle.afficher(); // Affiche tous les articles

            for (Article article : articles) {
                VBox articleBox = new VBox();
                articleBox.setSpacing(10);
                articleBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10;");

                // Titre
                Label titleLabel = new Label(article.getTitre());
                titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

                // Contenu
                //Label contentLabel = new Label(article.getContenue());
                //contentLabel.setWrapText(true);

                // Zone des commentaires
                VBox commentairesBox = new VBox();
                commentairesBox.setSpacing(5);
                commentairesBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 5;");


                Label commentsLabel = new Label("Commentaires :");
                commentsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

                TableView<Commentaire> table = new TableView<>();
                table.setPrefHeight(200);
                table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


// Colonne contenu
                TableColumn<Commentaire, String> contenuCol = new TableColumn<>("Contenu");
                contenuCol.setCellValueFactory(new PropertyValueFactory<>("contenue"));
                // contenuCol.setMinWidth(300);

// Colonne date
                TableColumn<Commentaire, Date> dateCol = new TableColumn<>("Date");
                dateCol.setCellValueFactory(new PropertyValueFactory<>("date_commentaire"));
                // dateCol.setMinWidth(150);

// Colonne ID ou score
                TableColumn<Commentaire, Integer> idCol = new TableColumn<>("ID");
                idCol.setCellValueFactory(new PropertyValueFactory<>("id_commentaire"));
                // idCol.setMinWidth(80);

// Ajouter colonnes à la table
                table.getColumns().addAll(contenuCol, dateCol, idCol);

// Charger les commentaires
                List<Commentaire> commentaires = serviceCommentaire.getCommentairesByArticle(article.getId());
                System.out.println(commentaires.size());
                //System.out.println("Article ID: " + article.getId());
                table.setItems(FXCollections.observableArrayList(commentaires));

// Ajouter dans le VBox
                articleBox.getChildren().addAll(titleLabel, commentsLabel, table);
                articlesContainer.getChildren().add(articleBox);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}