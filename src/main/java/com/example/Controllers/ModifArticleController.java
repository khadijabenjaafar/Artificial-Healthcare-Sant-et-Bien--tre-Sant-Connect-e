package com.example.Controllers;

import com.example.entities.Article;
import com.example.entities.Utilisateur;
import com.example.services.ServiceArticle;
import com.example.services.ServiceUtilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ModifArticleController {

    @FXML
    private VBox articlesContainer;

    //@FXML
   // private Label errorLabel;


    @FXML
    private ComboBox<Utilisateur> comboUtilisateur;



    private final ServiceArticle service = new ServiceArticle();
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    @FXML
    public void initialize() {
        try {
            List<Utilisateur> utilisateurs = serviceUtilisateur.afficher();
           // System.out.println("Utilisateurs chargés : " + utilisateurs); // 🔍 debug ici
           // comboUtilisateur.setItems(FXCollections.observableArrayList(utilisateurs));
            //comboUtilisateur.setOnAction(e -> afficherArticlesUtilisateur(comboUtilisateur.getValue()));
            ObservableList<Utilisateur> observableList = FXCollections.observableArrayList(utilisateurs) ;
            comboUtilisateur.setItems(observableList);

            comboUtilisateur.setOnAction(e -> {
                Utilisateur selectedUser = comboUtilisateur.getValue();
                if (selectedUser != null) {
                    afficherArticlesUtilisateur(selectedUser);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
           // errorLabel.setText("Erreur de chargement utilisateurs");
        }

    }





    private void afficherArticlesUtilisateur(Utilisateur utilisateur) {
        articlesContainer.getChildren().clear(); // VBox

        try {
            List<Article> articles = service.getArticlesByUser(utilisateur.getId());
            System.out.println("Articles trouvés : " + articles.size()); // 🪵 pour débug

            for (Article article : articles) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/articleItem1.fxml"));
                Parent card = loader.load();

                ArticleItem1Controller controller = loader.getController();
                controller.setArticle(article); // très important

                articlesContainer.getChildren().add(card);
            }
        } catch (Exception e) {
          //  errorLabel.setText("Erreur lors du chargement des articles.");
            e.printStackTrace();
        }
    }


    public void refreshAfterModification(Utilisateur utilisateur) {
        afficherArticlesUtilisateur(utilisateur);
    }

    //private void ouvrirInterfaceModification(Article article) {
      //  try {
        //    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/modifierArticle.fxml"));
         //   Parent root = loader.load();
          //  ModifierArticleController controller = loader.getController();
          //  controller.setArticle(article); // Transmet l'article sélectionné

            //Stage stage = new Stage();
            //stage.setScene(new Scene(root));
            //stage.setTitle("Modifier l'article");
            //stage.show();
        //} catch (IOException e) {
          //  e.printStackTrace();
        //}
    //}
















    //@FXML
    //private void retourVersMesArticles(ActionEvent event) {
        // Ferme la fenêtre actuelle
      //  Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
       // stage.close();
    //}




}















