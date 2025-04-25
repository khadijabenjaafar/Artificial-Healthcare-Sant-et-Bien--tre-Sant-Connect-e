


package org.example.controllers;

import org.example.entities.Article;
import org.example.services.ServiceArticle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ArticleItem1Controller{


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

    @FXML
    VBox maincontainer;













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



   // private ModifArticleController parentController;
   // private Utilisateur utilisateur; // celui du ComboBox

    //public void setParentController(ModifArticleController controller, Utilisateur utilisateur) {
     //   this.parentController = controller;
       // this.utilisateur = utilisateur;
    //}







    public void handleClick() {

        if (article != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modif_article.fxml"));
                Parent root = loader.load();

                ModifierArticleController controller = loader.getController();
                controller.setArticle(article); // envoie l'article à modifier
                //System.out.println(article.getTitre());


               // controller.setOnModificationDone(() -> {
                //   parentController.refreshAfterModification(utilisateur);
               // });

                Stage stage = (Stage) imageView.getScene().getWindow();
                stage.setTitle("Modifier l'article");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @FXML
    private void handleDelete() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'article");
        confirm.setContentText("Es-tu sûr de vouloir supprimer cet article ?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ServiceArticle service = new ServiceArticle();
                service.supprimer(article.getId()); // Assure-toi que l'article est bien stocké dans ce contrôleur
                // Optionnel : masquer ou supprimer le node de l'interface
                ((VBox) maincontainer.getParent()).getChildren().remove(maincontainer);; // ou articlesContainer.getChildren().remove(maincontainer);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }





    public void setOnClick(Runnable action) {
        this.onClick = action;
        maincontainer.setOnMouseClicked(event -> {
            if (onClick != null) onClick.run();
        });
    }

    @FXML
    private void handleReadMore() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Articledetails.fxml"));
            Parent root = loader.load();

            // Récupérer le controller de l'article details
            ArticledetailsController controller = loader.getController();
            controller.setArticle(article); // passe l'article sélectionné

            Stage stage = new Stage();
            stage.setTitle("Détails de l'article");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
