package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.entities.EnumRole;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;


import java.io.IOException;

public class DoctorController {
    @FXML
    private AnchorPane contentPane;
    @FXML
    private Label nom;






    @FXML
    private Hyperlink rdv;
    @FXML
    private Hyperlink cs;
    @FXML
    private Hyperlink css;
    @FXML
    private Hyperlink handelajouterFacture;
    @FXML
    private Hyperlink handleAfficherFacture;
    @FXML
    private Hyperlink handleAjouterOrdonnance;
    @FXML
    private Hyperlink handleAfficherOrdonnances;
    @FXML
    private Hyperlink handleAjouterArticle;
    @FXML
    private Hyperlink handleMesArticles;
    @FXML
    private Hyperlink handleAfficherPlanification;
    @FXML
    private Hyperlink handleAfficherMatching;

    @FXML
    private Hyperlink handlemodifArticles;
    @FXML
    private ImageView photo;
    public Utilisateur CurrentUser=UserConnecter.getInstance().getUserConnecter();
    @FXML
    public void initialize() {
        if (CurrentUser != null) {
            if (nom != null) {
                nom.setText(CurrentUser.getNom());
            } else {
                System.err.println("Le label 'nom' est null !");
            }

            if (photo != null && CurrentUser.getImage1() != null) {
                try {
                    photo.setImage(new Image(CurrentUser.getImage1()));
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image : " + CurrentUser.getImage1());
                    e.printStackTrace();
                }
            } else {
                System.err.println("L'image 'photo' est null ou le chemin de l'image est null !");
            }
            if (CurrentUser.getRole()== EnumRole.ROLE_MEDECIN)
            {
                rdv.setVisible(true);
                cs.setVisible(true);
                css.setVisible(true);
                handleAjouterOrdonnance.setVisible(true);
                handleAfficherOrdonnances.setVisible(true);
                handelajouterFacture.setVisible(false);
                handleAfficherFacture.setVisible(false);
                handleAjouterArticle.setVisible(true);
                handlemodifArticles.setVisible(true);
                handleAfficherMatching.setVisible(false);
                handleAfficherPlanification.setVisible(false);
              //  handleMesArticles.setVisible(true);
            }
            else if (CurrentUser.getRole()== EnumRole.ROLE_PHARMACIEN)
            {
                rdv.setVisible(false);
                cs.setVisible(false);
                css.setVisible(false);
                handleAjouterOrdonnance.setVisible(false);
                handleAfficherOrdonnances.setVisible(true);
                handelajouterFacture.setVisible(true);
                handleAfficherFacture.setVisible(true);
                handleAjouterArticle.setVisible(true);
                handlemodifArticles.setVisible(true);
                handleAfficherMatching.setVisible(false);
                handleAfficherPlanification.setVisible(false);
              //  handleMesArticles.setVisible(true);
            }else {
                rdv.setVisible(false);
                cs.setVisible(false);
                css.setVisible(false);
                handleAjouterOrdonnance.setVisible(false);
                handleAfficherOrdonnances.setVisible(false);
                handelajouterFacture.setVisible(false);
                handleAfficherFacture.setVisible(false);
                handleAjouterArticle.setVisible(true);
                handlemodifArticles.setVisible(true);
                handleAfficherMatching.setVisible(true);
                handleAfficherPlanification.setVisible(true);
               // handleMesArticles.setVisible(true);
            }
        } else {
            System.err.println("Aucun utilisateur connecté.");
        }
    }


    @FXML
    public void NavigateToCardRendezVous() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardRendezVous.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) contentPane.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAfficherMatching(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MatchingView.fxml"));
            Parent newView = loader.load();
            contentPane.getChildren().setAll(newView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAfficherPlanification(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlanificationView.fxml"));
            Parent newView = loader.load();
            contentPane.getChildren().setAll(newView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void rdv(ActionEvent actionEvent) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/CardRendezVous.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cs(ActionEvent actionEvent) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/AjouterConsultation.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void css(ActionEvent actionEvent) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/CardConsultation.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @FXML
    private void handleMesArticles(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/mesarticles.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handlemodifArticles(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ArticleDoctor.fxml")); // ✅ créer un vrai loader
            Parent fxml = loader.load();

            ArticlesDoctorController controller = loader.getController(); // ✅ accessible ici
            controller.setContentPane(contentPane); // 💡 tu lui donnes le contentPane

            contentPane.getChildren().setAll(fxml); // 🔄 affiche la page dans la zone
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handelajouterFacture(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/AjouterFacturation.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    private void handleAjouterArticle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajout-article.fxml")); // ✅ créer l'instance
            Parent fxml = loader.load(); // ✅ charger le fichier FXML

            ArticleajoutController controller = loader.getController(); // ✅ accéder au contrôleur
            controller.setContentPane(contentPane); // ✅ transmettre le contentPane

            contentPane.getChildren().clear(); // ou removeAll()
            contentPane.getChildren().setAll(fxml);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleAjouterOrdonnance(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/AjoutOrdonnance.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleAfficherOrdonnances(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/AfficheOrdonnance.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleAfficherFacture(ActionEvent event) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource("/AfficheFacturation.fxml"));
            contentPane.getChildren().removeAll();
            contentPane.getChildren().setAll(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void Deconnecter (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) contentPane.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



}

