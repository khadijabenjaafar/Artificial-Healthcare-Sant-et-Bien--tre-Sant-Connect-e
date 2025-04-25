package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.api.PasswordUtils;
import org.example.entities.Utilisateur;
import org.example.services.ServiceUtilisateur;

import java.io.IOException;

public class Reinitialiser {
    @FXML
    private PasswordField password;

    @FXML
    private Button reinitialiser;

    @FXML
    private PasswordField Copassword;
    @FXML
    private PasswordField passwordtemp;
    private Utilisateur utilisateur;
    public ServiceUtilisateur serviceUtilisateur=new ServiceUtilisateur();

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @FXML
    void Submit(ActionEvent event) {
        String pwd = password.getText();
        String confirmPwd = Copassword.getText();
        String pwdtmp= passwordtemp.getText();
        String hashed = PasswordUtils.hashPassword(pwdtmp); // simulate DB hash

        utilisateur = serviceUtilisateur.getUserByEmail(utilisateur.getEmail());

        String hashedPasswordFromDb = utilisateur.getPassword();
        if (PasswordUtils.checkPassword(pwdtmp, hashedPasswordFromDb)) {
            if (pwd == null || pwd.isEmpty() || !pwd.equals(confirmPwd)) {
                // Tu peux afficher une alerte ici si tu veux
                afficherErreur("Les champs ne peuvent pas être vides.");
                return;
            }
            // Vérifier que les mots de passe correspondent
            if (!pwd.equals(confirmPwd)) {
                afficherErreur("Les mots de passe ne correspondent pas.");
                return;
            }

            // Hacher le mot de passe
            String hashedPwd = PasswordUtils.hashPassword(pwd);

            // Mettre à jour
            ServiceUtilisateur su = new ServiceUtilisateur();
            boolean success = su.updatePassword(utilisateur.getId(), hashedPwd);

            if (success) {
                try {
                    // Redirection vers Login.fxml
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) reinitialiser.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("Erreur lors de la mise à jour du mot de passe.");
            }
        }else {
            afficherErreur("Le mot de passe temporaire est erroné.");
        }

    }
    private void afficherErreur(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
