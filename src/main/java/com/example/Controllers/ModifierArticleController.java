package com.example.Controllers;

import com.example.entities.Article;
import com.example.services.ServiceArticle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ModifierArticleController {

    @FXML
    private TextField titreField;
    @FXML
    private TextArea contenuField;

    private Article article;


    private Runnable onModificationDone;

     private final ServiceArticle serviceArticle=new ServiceArticle();
    

  //  public void setOnModificationDone(Runnable callback) {
    //    this.onModificationDone = callback;
    //}

    //@FXML
    //private ModifArticleController parentController;

    //public void setParentController(ModifArticleController controller) {
      //  this.parentController = controller;
    //}

    public void setArticle(Article article) {
        this.article = article;
        titreField.setText(article.getTitre());
        contenuField.setText(article.getContenue());
    }

    @FXML
    private void enregistrer(ActionEvent event) {
        article.setTitre(titreField.getText());
        article.setContenue(contenuField.getText());
        try {

           // ServiceArticle serviceArticle = null;
            serviceArticle.modifier(article);
            System.out.println("✅ Article modifié !");
            NavigateToliste();


            if (onModificationDone != null) {
                onModificationDone.run(); // 🔁 Appelle le rafraîchissement
            }




        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void NavigateToliste()  {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/avant-modif-article.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) titreField.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
