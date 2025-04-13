package com.example.Controllers;

import com.example.entities.Article;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ArticleItemController {


    @FXML
    private ImageView imageView;

    @FXML
    private Label titleLabel;

    @FXML
    private Label contentLabel;

    @FXML
    private Label dateLabel;


    private Article article;


    private Runnable onClick;


    VBox mainContainer;











    // méthode appelée depuis le controller parent
    public void setArticle(Article article) {
        this.article = article; // <--- cette ligne manquait !
        titleLabel.setText(article.getTitre());
        contentLabel.setText(article.getContenue());
        dateLabel.setText(article.getDateArticle().toString());

        // Affichage de l’image
        if (article.getUrlimagearticle() != null) {
            File file = new File(article.getUrlimagearticle());
            if (file.exists()) {
                Image image = new Image(file.toURI().toString(), 200, 0, true, true);
                imageView.setImage(image);
                System.out.println(file.exists());
            } else {
                System.out.println("Image not found at: " + file.getAbsolutePath());
            }
        }
    }






    public void setOnClick(Runnable action) {
        this.onClick = action;
        mainContainer.setOnMouseClicked(event -> {
            if (onClick != null) onClick.run();
        });
    }

    @FXML
    private void handleReadMore() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Articledetails.fxml"));
            Parent root = loader.load();

            // Récupérer le controller de l'article details
            ArticledetailsController controller = loader.getController();
            controller.setArticle(article); // passe l'article sélectionné

            Stage stage = (Stage) imageView.getScene().getWindow();
            stage.setTitle("Détails de l'article");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
