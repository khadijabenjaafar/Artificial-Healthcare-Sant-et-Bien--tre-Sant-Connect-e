
package org.example.controllers;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuBar;
import org.example.entities.Utilisateur;

public class MenuManager {




        // Cette méthode crée un menu dynamique en fonction du rôle de l'utilisateur
        public MenuBar createMenu(Utilisateur utilisateur) {
            // Crée une barre de menu
            MenuBar menuBar = new MenuBar();

            // Crée un menu de base
            Menu menu = new Menu("Menu");

            // Vérifie le rôle et ajoute les éléments correspondants
            if ("role_patient".equals(utilisateur.getRole())) {
                // Menu pour un patient
                menu.getItems().addAll(
                        new MenuItem("Ajouter rendez-vous"),
                        new MenuItem("Voir les rendez-vous"),
                        new MenuItem("Voir les articles"),
                        new MenuItem("Mes ordonnances"),
                        new MenuItem("Mes facturations"),
                        new MenuItem("Plannings")
                );
            } else if ("role_medecin".equals(utilisateur.getRole())) {
                // Menu pour un médecin
                menu.getItems().addAll(
                        new MenuItem("Ajouter consultation"),
                        new MenuItem("Voir consultation"),
                        new MenuItem("Ajouter ordonnances"),
                        new MenuItem("Mes ordonnances"),
                        new MenuItem("Ajouter article"),
                        new MenuItem("Mes articles")
                );
            } else if ("role_freelancer".equals(utilisateur.getRole())) {
                // Menu pour un freelancer
                menu.getItems().addAll(
                        new MenuItem("Ajouter matching"),
                        new MenuItem("Mes matchings"),
                        new MenuItem("Ajouter article"),
                        new MenuItem("Mes articles")
                );
            } else if ("role_pharmacien".equals(utilisateur.getRole())) {
                // Menu pour un pharmacien
                menu.getItems().addAll(
                        new MenuItem("Ajouter facturation"),
                        new MenuItem("Les facturations")
                );
            }

            // Ajouter le menu à la barre de menu
            menuBar.getMenus().add(menu);

            // Retourner le MenuBar créé
            return menuBar;
        }

        // Méthode utilitaire pour créer un MenuItem




}
