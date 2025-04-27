package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.api.PasswordUtils;
import org.example.api.mailler;
import org.example.entities.Status;
import org.example.entities.Utilisateur;
import org.example.services.ServiceUtilisateur;
import  org.example.entities.UserConnecter;

public class Login {
    @FXML
    private TextField Email;

    @FXML
    private Button loginBtn;

    @FXML
    private Label navsignup;

    @FXML
    private PasswordField password;
    private int loginAttempts=0;
    ServiceUtilisateur serviceUtilisateur=new ServiceUtilisateur();




    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void Submit(ActionEvent event) {
        // Retrieve email and password from the input fields
        String email = Email.getText();
        String password1 = password.getText();
        if (email.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Error", "Veuillez entrer un email.");
            return;
        }

        Utilisateur user = serviceUtilisateur.getUserByEmail(email);

        if (user == null) {
            showAlert(AlertType.ERROR, "Login Failed", "Utilisateur introuvable.");
            return;
        }
        if (Status.BANNED.equals(user.getStatus())) {
            showAlert(AlertType.ERROR, "Login Failed", "Votre compte est banni.");
            return;
        }
        if (password1.isEmpty()) {
            String imageUrl2 = "src/main/resources/comparaison/" + email + ".jpg";

            // Script Python pour comparer les visages
            String scriptPath = "src/main/java/org/example/api/faceid.py";

            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, imageUrl2, user.getImage()); // ajoute le chemin de l'image de référence
            pb.redirectErrorStream(true);
            System.out.println("SCRIPT PATH: " + scriptPath);
            System.out.println("CAPTURED IMAGE PATH: " + imageUrl2);
            System.out.println("REGISTERED IMAGE PATH: " + user.getImage());
            try {
                Process process = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output = reader.readLine(); // la sortie du script (ex: "True" ou "False")
                System.out.println("SCRIPT OUTPUT: " + output);
                boolean isMatch = output != null && output.contains("\"match\": \"true\"");
                System.out.println("SCRIPT isMatch: " + isMatch);

                if (isMatch) {
                    showAlert(AlertType.INFORMATION, "Login Successful", "Bienvenue, " + user.getNom() + " !");
                    UserConnecter.getInstance().setUserConnecter(user);
                    navigateByRole(user);
                } else {
                    loginAttempts++;
                    if (loginAttempts >= 3) {
                        captureImageAndSendMail();
                        loginAttempts = 0; // reset après l'envoi
                    } else {
                        System.out.println("Mot de passe incorrect. Tentative " + loginAttempts + "/3");
                    }
                    showAlert(AlertType.ERROR, "Login Failed", "Reconnaissance faciale échouée.");
                }

                process.waitFor(); // attends la fin du script
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la comparaison faciale.");
            }
        }
        else {
            String hashedPasswordFromDb = user.getPassword();
            if (PasswordUtils.checkPassword(password1, hashedPasswordFromDb)) {
                showAlert(AlertType.INFORMATION, "Login Successful", "Welcome, " + user.getNom() + "!");

                UserConnecter.getInstance().setUserConnecter(user);
                navigateByRole(user);

            } else {
                loginAttempts++;
                if (loginAttempts >= 3) {
                    captureImageAndSendMail();
                    loginAttempts = 0; // reset après l'envoi
                } else {
                    System.out.println("Mot de passe incorrect. Tentative " + loginAttempts + "/3");
                }
                showAlert(AlertType.ERROR, "Login Failed", "Invalid email or password.");
            }
        }

    }
    public void navigateByRole(Utilisateur user){
        if(user.getRole().toString().equals("ROLE_ADMIN")){
            navigateToBackAdmin();
        } else if (user.getRole().toString().equals("ROLE_FREELANCER")||user.getRole().toString().equals("ROLE_PHARMACIEN")||user.getRole().toString().equals("ROLE_MEDECIN")) {
            navigateToDoctor();
        }
        else{
            navigateToHome();
        }
    }
    public void captureImageAndSendMail() {
        String imagePath = "src/main/resources/intrues/" +Email.getText()+ ".jpg";
        captureImage(imagePath);
        mailler.sendEmailWithAttachment(Email.getText(), imagePath);
    }

    public void captureImage(String imagePath){
            try {
                String scriptPath = "src/main/java/org/example/api/capture_face.py";
                ProcessBuilder pb = new ProcessBuilder("python", scriptPath, imagePath);

                pb.redirectErrorStream(true);
                Process process = pb.start();

                // Attente de la fin du script
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("Image capturée avec succès !");

                    // Optionnel : afficher ou traiter l’image
                    File imageFile = new File(imagePath);
                } else {
                    System.out.println("Erreur lors de la capture de l’image.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    private void navigateToHome() {
        try {
            // Load the home.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Newindex.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) loginBtn.getScene().getWindow();

            // Set the new scene with the home.fxml content
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Unable to load the home screen.");
        }
    }
    private void navigateToDoctor() {
        try {
            // Load the home.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/doctor.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) loginBtn.getScene().getWindow();

            // Set the new scene with the home.fxml content
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Unable to load the home screen.");
        }
    }

    private void navigateToBackAdmin() {
        try {
            // Load the home.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/BackAdmin.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) loginBtn.getScene().getWindow();

            // Set the new scene with the home.fxml content
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Unable to load the home screen.");
        }
    }

    public void navigateToSignUp(MouseEvent mouseEvent) {
        try {
            // Load the SignUpView.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreerCompte.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) navsignup.getScene().getWindow();

            // Set the new scene with the SignUpView.fxml content
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Unable to load the sign-up screen. Please check the file path.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "The SignUpView.fxml file was not found. Please check the file path.");
        }
    }

    @FXML
    void oublier(ActionEvent event) {
        try {
            // Load the home.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgetPassword.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) loginBtn.getScene().getWindow();

            // Set the new scene with the home.fxml content
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Unable to load the home screen.");
        }
    }
    @FXML
    public void faceId()
    {
        try {
            String imageUrl2 = "src/main/resources/comparaison/" +Email.getText()+ ".jpg";
            String scriptPath = "src/main/java/org/example/api/capture_face.py";
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, imageUrl2);

            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Attente de la fin du script
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                showAlert(Alert.AlertType.INFORMATION, "Image de la face ID", "Image capturée avec succès.");
                System.out.println("Image capturée avec succès !");
            } else {
                showAlert(Alert.AlertType.ERROR, "Image de la face ID", "Erreur lors de la capture de l’image.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
