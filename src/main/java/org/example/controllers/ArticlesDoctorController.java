package org.example.controllers;

import javafx.scene.layout.Pane;
import org.example.entities.Article;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import org.example.services.ServiceArticle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.util.List;

public class ArticlesDoctorController {
    @FXML
    private VBox articlesContainer;

    public Utilisateur CurrentUser = UserConnecter.getInstance().getUserConnecter();

// Plus besoin de ComboBox
// @FXML
// private ComboBox<Utilisateur> comboUtilisateur;
    @FXML
 private Pane contentPane;



    public void setContentPane(Pane contentPane) {
        this.contentPane = contentPane;

        // ✅ Quand le contentPane est prêt, tu peux afficher les articles
        afficherArticlesUtilisateur(CurrentUser);
    }

    private final ServiceArticle service = new ServiceArticle();

    @FXML
    public void initialize() {
        try {
            // Chargement automatique des articles de l'utilisateur connecté
            afficherArticlesUtilisateur(CurrentUser);
        } catch (Exception e) {
            e.printStackTrace();
            // Optionnel : afficher une erreur visuelle
            // errorLabel.setText("Erreur de chargement des articles.");
        }
    }

    private void afficherArticlesUtilisateur(Utilisateur utilisateur) {
        articlesContainer.getChildren().clear();

        try {
            List<Article> articles = service.getArticlesByUser(utilisateur.getId());
           // System.out.println("Articles trouvés : " + articles.size());

            for (Article article : articles) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/articleItem1.fxml"));
                Parent card = loader.load();

                ArticleItem1Controller controller = loader.getController();

                controller.setContentPane(contentPane); // 💡 c'est ici qu'on rend contentPane dispo
                controller.setArticle(article);
                if(contentPane==null){
                    System.out.println("nuleee kbal ");
                }
               // controller.setArticle(article);

                articlesContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Optionnel : afficher une erreur visuelle
            // errorLabel.setText("Erreur lors du chargement des articles.");
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















