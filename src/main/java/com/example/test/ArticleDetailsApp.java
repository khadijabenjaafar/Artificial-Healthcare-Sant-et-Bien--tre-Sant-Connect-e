package com.example.test;

import com.example.Controllers.ArticledetailsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class ArticleDetailsApp  {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/DetailsArticle.fxml"));
   // Parent root = loader.load();

    ArticledetailsController controller = loader.getController();
    // controller.setArticle(article); // Passe l'article ici

    Stage stage = new Stage();
   // stage.setTitle("Détails de l'article");
   // stage.setScene(new Scene(root));
  //  stage.show();

}
