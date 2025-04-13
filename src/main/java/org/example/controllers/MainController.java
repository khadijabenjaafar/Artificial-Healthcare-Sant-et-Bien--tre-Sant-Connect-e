package org.example.controllers;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.MenuBar;
import org.example.entities.Utilisateur;

public class MainController {



        private Utilisateur utilisateur;  // Utilisateur actuel (en général, tu le récupères après la connexion)
        private MenuManager menuManager;

        // Exemple de constructeur
        public MainController() {
            // L'utilisateur pourrait être initialisé après la connexion (ici un exemple simple)
            this.utilisateur = new Utilisateur("role_patient"); // Remplacer par l'utilisateur actuel
            this.menuManager = new MenuManager();
        }

        @FXML
        private BorderPane rootLayout;  // Cela fait référence à la racine de ton layout (par exemple un BorderPane)

        // Méthode pour initialiser la scène
        public void initialize() {
            // Créer le menu dynamique en fonction du rôle de l'utilisateur
            MenuBar menuBar = menuManager.createMenu(utilisateur);

            // Ajouter le menu à la partie supérieure du BorderPane
            rootLayout.setTop(menuBar);
        }


}
