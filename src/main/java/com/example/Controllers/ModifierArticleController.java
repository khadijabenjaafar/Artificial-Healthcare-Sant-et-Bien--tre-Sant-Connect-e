package com.example.Controllers;

import com.example.entities.Article;
import com.example.services.ServiceArticle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ModifierArticleController {

    @FXML
    private TextField titreField;
    @FXML
    private TextArea contenuArea;

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
        contenuArea.setText(article.getContenue());
    }

    @FXML
    private void enregistrer(ActionEvent event) {
        article.setTitre(titreField.getText());
        article.setContenue(contenuArea.getText());
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


    @FXML
    private Label urlImageLabel;

    @FXML
    private void insererImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            article.setUrlimagearticle(selectedFile.getAbsolutePath());
            urlImageLabel.setText(selectedFile.getName());
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
