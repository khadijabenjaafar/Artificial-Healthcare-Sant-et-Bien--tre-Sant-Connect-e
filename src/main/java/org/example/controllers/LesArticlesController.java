package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.entities.Article;
import org.example.services.ServiceArticle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class LesArticlesController {


    @FXML
    private FlowPane articlesContainer;

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
    public void retour(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Newindex.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) articlesContainer.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
