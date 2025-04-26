package org.example.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.entities.EnumRole;
import org.example.entities.Notification;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import org.example.services.ServiceNotification;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class IndexFront {
    @FXML
    private Button inscrire;

    @FXML
    private ScrollPane scrollPane;

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
    private ImageView notificationIcon;

    @FXML
    private void handleNotificationClick() {
        try {
            List<Notification> notifications = new ServiceNotification().getUnreadForUser(CurrentUser);
            if (notifications.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Aucune nouvelle notification.");
                alert.showAndWait();
            } else {
                StringBuilder message = new StringBuilder();
                for (Notification n : notifications) {
                    message.append("- ").append(n.getMessage()).append("\n");
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION, message.toString());
                alert.setHeaderText("Notifications");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleChatClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/chat.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Messagerie Instantanée");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
