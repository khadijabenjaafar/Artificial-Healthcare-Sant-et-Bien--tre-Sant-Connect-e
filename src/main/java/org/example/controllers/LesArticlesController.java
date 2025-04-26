package org.example.controllers;

import org.example.entities.Article;
import org.example.services.ServiceArticle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.util.List;

public class LesArticlesController {


    @FXML
    private VBox articlesContainer;

    public void initialize() {
        ServiceArticle service = new ServiceArticle();

        try {
            List<Article> articles = service.afficher();

            for (Article article : articles) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/articleItem.fxml"));
                Parent card = loader.load();

                ArticleItemController controller = loader.getController();

                controller.setArticle(article);

                articlesContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
