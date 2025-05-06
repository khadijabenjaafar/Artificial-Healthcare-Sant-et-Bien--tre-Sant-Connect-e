package org.example.controllers;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import org.example.entities.Article;
import org.example.services.ServiceArticle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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


    private Pane contentPane;
    private Article selectedArticle;

    public void setContentPane(Pane contentPane) {
        this.contentPane = contentPane;
    }


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
        this.selectedArticle = article;
        this.article = article;
        titreField.setText(article.getTitre());
        contenuArea.setText(article.getContenue());
    }

    @FXML
    private void enregistrer(ActionEvent event) {
        article.setTitre(titreField.getText());
        article.setContenue(contenuArea.getText());
        try {
            serviceArticle.modifier(article);
            System.out.println("✅ Article modifié !");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ArticleDoctor.fxml"));
            Parent root = loader.load();

            // Aller vers la nouvelle page
            Stage stage = (Stage) titreField.getScene().getWindow(); // on récupère la fenêtre actuelle
            stage.setScene(new Scene(root));
            stage.show();


            // ArticlesDoctorController controller = loader.getController();
            // controller.setContentPane(contentPane); // 🟢 transmettre le contentPane

            // contentPane.getChildren().setAll(fxml); // 🔁 afficher la vue

            if (onModificationDone != null) {
                onModificationDone.run(); // 🔄 relancer une action si définie
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("❌ Erreur lors de la modification : " + e.getMessage());
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ArticleDoctor.fxml"));
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

