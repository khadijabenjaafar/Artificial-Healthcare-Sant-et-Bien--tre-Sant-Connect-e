package com.example.Controllers;

import com.example.entities.Article;
import com.example.services.ServiceArticle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class mesArticlesController {


    @FXML
    private VBox articlesContainer;

    public void initialize() {
        ServiceArticle service = new ServiceArticle();

        try {
            List<Article> articles = service.afficher();

            for (Article article : articles) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/articleItem.fxml"));
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
