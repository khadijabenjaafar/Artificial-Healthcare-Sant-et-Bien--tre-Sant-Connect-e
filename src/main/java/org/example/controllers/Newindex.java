package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.entities.EnumRole;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.entities.EnumRole;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import java.io.IOException;
import java.sql.SQLException;

public class Newindex {
    @FXML
    private Button inscrire;

    @FXML
    private Button seConnecter;

    public Utilisateur CurrentUser=UserConnecter.getInstance().getUserConnecter();

    private Stage currentStage;
    public void setStage(Stage stage) {
        this.currentStage = stage;
    }


    @FXML
    public void initialize() {
        checkUserConnection();
    }
    @FXML
    void NavigateTosignUp(ActionEvent event) throws IOException {
        try {
            if (CurrentUser!= null && CurrentUser.getRole() == EnumRole.ROLE_PATIENT) {
                // Load the home.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                Parent root = loader.load();

                // Get the current stage (window)
                Stage stage = (Stage) inscrire.getScene().getWindow();

                // Set the new scene with the home.fxml content
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();}
            else {
                // Load the home.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreerCompte.fxml"));
                Parent root = loader.load();

                // Get the current stage (window)
                Stage stage = (Stage) inscrire.getScene().getWindow();

                // Set the new scene with the home.fxml content
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    @FXML
    void NavigateTosignIn(ActionEvent event) throws SQLException, IOException {
        try {
            System.out.println("CurrentUser = " + CurrentUser);
            if (CurrentUser != null) {
                System.out.println("Role = " + CurrentUser.getRole());
            }
            if (CurrentUser!= null && CurrentUser.getRole() == EnumRole.ROLE_PATIENT) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
                Parent parent = loader.load();
                Profile profilControllers= loader.getController();
                profilControllers.setNom(CurrentUser.getNom());
                profilControllers.setPrenom(CurrentUser.getPrenom());
                profilControllers.setAdresse(CurrentUser.getAdresse());
                profilControllers.setEmail(CurrentUser.getEmail());
                profilControllers.setImage1(CurrentUser.getImage1());


                profilControllers.setGenre(CurrentUser.getGenre());
                profilControllers.setNumtel(CurrentUser.getnumTel());
                profilControllers.setDate(CurrentUser.getDate_naissance());

                Stage stage = (Stage) seConnecter.getScene().getWindow();
                Scene scene = new Scene(parent);
                stage.setScene(scene);
                stage.show();
            } else {
                // Redirige vers login.fxml si l'utilisateur n'est pas connecté
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) seConnecter.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void rendezVous(MouseEvent event) {

        if (CurrentUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Connexion requise");
            alert.setHeaderText(null);
            alert.setContentText("❗ Vous devez être connecté.");
            alert.showAndWait();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                Parent loginView = loader.load();
                Stage stage = (Stage) inscrire.getScene().getWindow();
                Scene scene = new Scene(loginView);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardRendezVous.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) inscrire.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void checkUserConnection() {
        if (CurrentUser!= null && "ROLE_PATIENT".equals(CurrentUser.getRole().toString())) {
            inscrire.setText("Se deconnecter");
            seConnecter.setText("Profile");
        } else {
            inscrire.setText("S'inscrire");
            seConnecter.setText("Se connecter");
        }
    }

    @FXML
    void articles(MouseEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mesarticles.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) inscrire.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void factures(MouseEvent event) {
        if (CurrentUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Connexion requise");
            alert.setHeaderText(null);
            alert.setContentText("❗ Vous devez être connecté.");
            alert.showAndWait();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                Parent loginView = loader.load();
                Stage stage = (Stage) inscrire.getScene().getWindow();
                Scene scene = new Scene(loginView);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheFacturation.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) inscrire.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void freelancers(MouseEvent event) {
        if (CurrentUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Connexion requise");
            alert.setHeaderText(null);
            alert.setContentText("❗ Vous devez être connecté.");
            alert.showAndWait();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                Parent loginView = loader.load();
                Stage stage = (Stage) inscrire.getScene().getWindow();
                Scene scene = new Scene(loginView);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/freelancer.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) inscrire.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ordonnances(MouseEvent event) {
        if (CurrentUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Connexion requise");
            alert.setHeaderText(null);
            alert.setContentText("❗ Vous devez être connecté.");
            alert.showAndWait();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                Parent loginView = loader.load();
                Stage stage = (Stage) inscrire.getScene().getWindow();
                Scene scene = new Scene(loginView);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheOrdonnance.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) inscrire.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void apropos(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/indexFront.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) inscrire.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
