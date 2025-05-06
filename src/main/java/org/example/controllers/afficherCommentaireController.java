package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Orientation;
import org.example.entities.Article;
import org.example.entities.Commentaire;
import org.example.services.ServiceArticle;
import org.example.services.ServiceCommentaire;

import java.util.List;

public class afficherCommentaireController {

    @FXML
    private VBox articlesContainer;

    private final ServiceArticle serviceArticle = new ServiceArticle();
    private final ServiceCommentaire serviceCommentaire = new ServiceCommentaire();

    @FXML
    public void initialize() {
        try {
            List<Article> articles = serviceArticle.afficher(); // Tous les articles

            for (Article article : articles) {
                VBox articleBox = new VBox();
                articleBox.setSpacing(10);
                articleBox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10;");

                // Titre article
                Label titleLabel = new Label(article.getTitre());
                titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

                // Label "Commentaires"
                Label commentsLabel = new Label("Commentaires :");
                commentsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

                // Récupération des commentaires
                List<Commentaire> commentaires = serviceCommentaire.getCommentairesByArticle(article.getId());

                // ListView horizontal
                ListView<Commentaire> listView = new ListView<>();
                listView.setPrefHeight(100);
                listView.setPrefWidth(800); // largeur totale
                listView.setOrientation(Orientation.HORIZONTAL);
                listView.setItems(FXCollections.observableArrayList(commentaires));

                // Cellule personnalisée
                listView.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(Commentaire commentaire, boolean empty) {
                        super.updateItem(commentaire, empty);
                        if (empty || commentaire == null) {
                            setGraphic(null);
                        } else {
                            VBox cell = new VBox();
                            cell.setSpacing(5);
                            cell.setPrefWidth(200); // largeur de chaque carte
                            cell.setStyle(
                                    "-fx-background-color: #ffffff;" +
                                            "-fx-border-color: #ddd;" +
                                            "-fx-border-radius: 10;" +
                                            "-fx-background-radius: 10;" +
                                            "-fx-padding: 10;" +
                                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0.3, 0, 1);"
                            );

                            Label contenu = new Label("📝 " + commentaire.getContenue());
                            contenu.setStyle("-fx-font-size: 13px;");

                            Label date = new Label("📅 " + commentaire.getDateCommentaire());
                            date.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

                            cell.getChildren().addAll(contenu, date);
                            setGraphic(cell);
                        }
                    }
                });

                // Ajouter à la vue
                articleBox.getChildren().addAll(titleLabel, commentsLabel, listView);
                articlesContainer.getChildren().add(articleBox);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
