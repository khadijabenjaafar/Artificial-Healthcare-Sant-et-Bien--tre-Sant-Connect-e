package org.example.controllers;


import org.example.entities.Article;
import org.example.entities.Utilisateur;
import org.example.services.ServiceArticle;
import org.example.services.ServiceUtilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ArticleajoutController {

    @FXML
    private TextField titreField;

    @FXML
    private TextArea contenueArea;

    @FXML
    private Button btnChoisirImage;

    @FXML
    private Label labelImage;

    @FXML
    private ComboBox<Utilisateur> comboUtilisateur;


    @FXML
    private Label errorTitre;

    @FXML
    private Label errorContenue;

    @FXML
    private Label errorImage;



    @FXML
    private Button btnEnregistrer;

    private File imageChoisie;
    private final ServiceArticle serviceArticle = new ServiceArticle();
    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    @FXML
    public void initialize() {
        // Charger la liste des utilisateurs depuis la base
        try {
            List<Utilisateur> utilisateurs = serviceUtilisateur.afficher();
            ObservableList<Utilisateur> observableList = FXCollections.observableArrayList(utilisateurs) ;
            comboUtilisateur.setItems(observableList);
        } catch (SQLException e) {
            showAlert("❌ Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }

        // Choisir image
        btnChoisirImage.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
            );
            imageChoisie = fileChooser.showOpenDialog(btnChoisirImage.getScene().getWindow());
            if (imageChoisie != null) {
                try {
                    String destinationPath = "images/" + imageChoisie.getName();
                    File dest = new File(destinationPath);
                    Files.copy(imageChoisie.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    labelImage.setText(imageChoisie.getName());
                    imageChoisie = dest;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showAlert("Erreur lors de la copie de l'image : " + ex.getMessage());
                }
            }
        });

        // Enregistrer article
        btnEnregistrer.setOnAction(e -> {
            try {
                enregistrerArticle();
            } catch (SQLException ex) {
                showAlert("❌ Erreur SQL : " + ex.getMessage());
            }
        });
    }

    private void enregistrerArticle() throws SQLException {

        // Réinitialiser le message
        errorTitre.setText("");
        errorContenue.setText("");
        errorImage.setText("");


        String titre = titreField.getText();
        String contenue = contenueArea.getText();
    Utilisateur utilisateur = comboUtilisateur.getValue();



        if (titre.isEmpty() || contenue.isEmpty() || utilisateur == null || imageChoisie == null) {
          showAlert("⚠️ Tous les champs sont obligatoires.");
         return;
        }







        if (titre.isEmpty()) {
            errorTitre.setText("Le titre est requis.");
            return;
        }

        if (contenue.isEmpty()) {
            errorContenue.setText("Le contenu est requis.");
            return;
        }

        if (titre.length() < 3) {
            errorTitre.setText("Le titre doit contenir au moins 3 caractères.");
            return;
        }

        if (contenue.length() < 10) {
            errorContenue.setText("Le contenu est trop court.");
            return;
        }


        if (imageChoisie == null) {
            errorImage.setText("Selectionner une photo");
            return;
        }





        //if (titre.isEmpty() || contenue.isEmpty() || utilisateur == null || imageChoisie == null) {
          //  showAlert("⚠️ Tous les champs sont obligatoires.");
           // return;
        //}

        Article article = new Article();
        article.setTitre(titre); // si ton entité a un champ titre
        article.setContenue(contenue);
        article.setUrlimagearticle("images/" + imageChoisie.getName());
        article.setDateArticle(Date.valueOf(LocalDate.now()));
        article.setNbreVue(0);
        article.setUrlimagearticle(imageChoisie.getAbsolutePath()); // ou juste le nom si tu préfères
        article.setUtilisateur(utilisateur);

        serviceArticle.ajouter(article);
        showAlert("✅ Article ajouté avec succès !");
        System.out.println(article);
        resetForm();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }

    private void resetForm() {
        titreField.clear();
        contenueArea.clear();
        labelImage.setText("");
        comboUtilisateur.getSelectionModel().clearSelection();
        imageChoisie = null;
    }
}