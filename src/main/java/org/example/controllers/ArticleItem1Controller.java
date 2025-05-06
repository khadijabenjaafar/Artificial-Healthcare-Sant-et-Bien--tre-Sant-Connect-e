package org.example.controllers;

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
import org.example.entities.Article;
import org.example.services.ServiceArticle;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ArticleItem1Controller {

    @FXML
    private VBox maincontainer;

    @FXML
    private ImageView imageView;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label summaryLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label viewsLabel;

    @FXML
    private Label commentsLabel;

    private Article article;

    // Méthode pour recevoir l'article depuis le controller parent
    public void setArticle(Article article) throws SQLException {
        this.article = article;

        if (article == null) return;

        titleLabel.setText(article.getTitre());
        summaryLabel.setText(resumerContenu(article.getContenue(), 100));
        dateLabel.setText(article.getDateArticle().toString());
        viewsLabel.setText(String.valueOf(article.getNbreVue()));
        // commentsLabel.setText(String.valueOf(article.getCommentaire()));
        // categoryLabel.setText(article.getUtilisateur() != null ? article.getC() : "Non spécifiée");

        if (article.getUrlimagearticle() != null) {
            File file = new File(article.getUrlimagearticle());
            if (file.exists()) {
                Image image = new Image(file.toURI().toString(), 260, 150, true, true);
                imageView.setImage(image);
            } else {
                System.out.println("Image non trouvée : " + file.getAbsolutePath());
            }
        }
    }

    private String resumerContenu(String contenu, int maxLength) {
        if (contenu == null) return "";
        if (contenu.length() <= maxLength) return contenu;
        return contenu.substring(0, maxLength) + "...";
    }

    @FXML
    private void handleReadMore() {
        if (article == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Articledetails.fxml"));
            Parent root = loader.load();

            ArticledetailsController controller = loader.getController();
            controller.setArticle(article);

            Stage stage = (Stage) titleLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClick() {
        if (article == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modif_article.fxml"));
            Parent root = loader.load();

            ModifierArticleController controller = loader.getController();
            controller.setArticle(article);

            Stage stage = (Stage) maincontainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
                service.supprimer(article.getId());

                // 🔥 CORRECTION ICI
                ((javafx.scene.layout.Pane) maincontainer.getParent()).getChildren().remove(maincontainer);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}