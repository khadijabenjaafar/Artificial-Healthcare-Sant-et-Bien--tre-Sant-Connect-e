package org.example.controllers;

import org.example.entities.Article;
import org.example.entities.Commentaire;
import org.example.entities.Utilisateur;
import org.example.services.ServiceCommentaire;
import org.example.services.ServiceUtilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class ArticledetailsController {
    @FXML
    private Label labelTitre;

    @FXML
    private ImageView imageArticle;

    @FXML
    private Label contenuArticle;


    @FXML
    private Label dateArticle;

    @FXML
    private TextFlow contenuTextFlow;



    @FXML
    private ComboBox<Utilisateur> comboUtilisateur;





    @FXML
    private VBox commentairesContainer;

    @FXML
    private Label errorCommentaire;

    @FXML
    private TextField champCommentaire;

    private Article article;

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();




    // Appelée automatiquement quand le FXML est chargé
    @FXML
    private void initialize() {
        try {
            List<Utilisateur> utilisateurs = serviceUtilisateur.afficher();
            ObservableList<Utilisateur> observableList = FXCollections.observableArrayList(utilisateurs) ;
            comboUtilisateur.setItems(observableList);
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }

        // Pas besoin de faire quoi que ce soit ici si on n'a pas encore reçu l'article
    }

    // Méthode appelée depuis ArticleItemController
    public void setArticle(Article article) {
        this.article = article;

        // Vérifie que les éléments FXML ne sont pas nuls avant de les utiliser
        if (labelTitre != null && contenuTextFlow != null && dateArticle != null) {
            labelTitre.setText(article.getTitre());

            contenuTextFlow.getChildren().clear();
            Text contenu = new Text(article.getContenue());
            contenu.setWrappingWidth(560);
            contenu.setStyle("-fx-font-size: 14px;");
            contenuTextFlow.getChildren().add(contenu);

            dateArticle.setText(article.getDateArticle().toString());

            if (article.getUrlimagearticle() != null) {
                File file = new File(article.getUrlimagearticle());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageArticle.setImage(image);
                } else {
                    System.out.println("Image not found: " + file.getAbsolutePath());
                }
            }
        }
        afficherCommentaires();
    }

    @FXML
    private void envoyerCommentaire() {
        String texte = champCommentaire.getText().trim();
        if (!texte.isEmpty()) {
            Commentaire c = new Commentaire();
            c.setContenue(texte);
            c.setUtilisateur(comboUtilisateur.getSelectionModel().getSelectedItem());
            c.setArticle(article); // 💡 lien avec l'article affiché
            c.setDateCommentaire(Date.valueOf(LocalDate.now()));
            c.setHeure(Time.valueOf(LocalTime.now()));
          //  c.setDateCommentaire(Date.now());

            ServiceCommentaire service = new ServiceCommentaire();
            try {
                service.ajouter(c); //  Enregistrer en base
                champCommentaire.clear();
                System.out.println("Commentaire enregistré !");
                afficherCommentaires();
                champCommentaire.clear();
                System.out.println("Commentaire enregistré !");

                // Optionnel : rafraîchir l’affichage
                afficherCommentaires();// pour rafraîchir les commentaires affichés
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de l'ajout du commentaire.");
            }





        } else {
            errorCommentaire.setText("Chmaps vide");
            System.out.println("Champ vide.");
        }
    }



    private void afficherCommentaires() {

        commentairesContainer.getChildren().clear();
        ServiceCommentaire serviceCommentaire = new ServiceCommentaire();
        List<Commentaire> commentaires = serviceCommentaire.getCommentairesByArticle(article.getId());
        for (Commentaire commentaire : commentaires) {
          //  System.out.println(commentaire.getDateCommentaire());
           // System.out.println(commentaire.getIdCommentaire());

            String nomPrenom = commentaire.getUtilisateur().getPrenom() + " " + commentaire.getUtilisateur().getNom();
            Label labelNom = new Label(nomPrenom);
            labelNom.setStyle("-fx-font-weight: bold;");

            Label label = new Label(commentaire.getContenue());
            label.setWrapText(true);
            label.setStyle("-fx-background-color: #E8EAF6; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dateTexte = (commentaire.getDateCommentaire() != null) ?
                    sdf.format(commentaire.getDateCommentaire()) : "Date inconnue";

            String heureTexte = (commentaire.getHeure() != null) ?
                    commentaire.getHeure().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "Heure inconnue";

            Label labelDate = new Label(dateTexte + " à " + heureTexte);
            labelDate.setStyle("-fx-text-fill: #777777; -fx-font-size: 10px;");



            // Groupe de commentaires
            VBox commentaireBox = new VBox(labelNom,label, labelDate);
            commentaireBox.setSpacing(5);



            // Bouton menu ⋮
            MenuButton menuButton = new MenuButton();
            MenuItem modifier = new MenuItem("Modifier");
            MenuItem supprimer = new MenuItem("Supprimer");
            menuButton.getItems().addAll(modifier, supprimer);
            menuButton.setStyle("-fx-background-color: transparent; -fx-font-size: 18px;");

            // Action Supprimer
            supprimer.setOnAction(e -> {
                try {
                    serviceCommentaire.supprimer(commentaire.getIdCommentaire());
                    afficherCommentaires(); // Rafraîchir l’affichage
                    System.out.println(commentaire.getIdCommentaire());
                    System.out.println(commentaire.getHeure());

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            // Action Modifier (tu peux ouvrir une popup ou champ editable ici)
            modifier.setOnAction(e -> {
                TextField champModif = new TextField(commentaire.getContenue());
                champModif.setPrefWidth(400); // largeur du champ
                Button btnSave = new Button("💾Modifier");

                // Action enregistrer
                btnSave.setOnAction(ev -> {
                    commentaire.setContenue(champModif.getText());
                    try {
                        System.out.println(commentaire.getIdCommentaire());
                        System.out.println(commentaire.getHeure());
                        System.out.println(commentaire.getArticle().getId());
                        new ServiceCommentaire().modifier(commentaire); // tu dois créer cette méthode dans ton service
                        afficherCommentaires(); // rafraîchit
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                // Affiche le champ et bouton à la place du label de contenu
                commentaireBox.getChildren().clear();
                commentaireBox.getChildren().addAll(labelNom, champModif, btnSave, labelDate);
            });

            // Espace pour pousser le menu à droite
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // HBox contenant contenu + menu
            HBox ligne = new HBox(commentaireBox, spacer, menuButton);
            ligne.setSpacing(10);
            ligne.setAlignment(Pos.CENTER_LEFT);

            commentairesContainer.getChildren().add(ligne);
         // commentairesContainer.getChildren().add(commentaireBox);
        }














        }



        }








