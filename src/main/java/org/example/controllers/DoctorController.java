package org.example.controllers;

import com.calendarfx.view.CalendarView;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.entities.EnumRole;
import org.example.entities.Notification;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import org.example.services.ServiceMessage;
import org.example.services.ServiceNotification;
import org.example.services.ServiceRendezVous;
import org.example.utils.NavigationUtil;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class DoctorController {

    @FXML
    private Label nom;

    @FXML
    private Hyperlink css;

    @FXML
    private Hyperlink handleAfficherFacture;

    @FXML
    private Hyperlink handleAfficherOrdonnances;

    @FXML
    private Hyperlink handleAfficherPlanification;
    @FXML
    private Hyperlink handleAfficherMatching;
    @FXML
    private Label chatBadge; // ✅ Label for badge

    @FXML
    private ImageView notificationIcon;
    private Timeline timeline; // ✅ Auto refresh
    @FXML
    private Hyperlink handlemodifArticles;
    @FXML
    private CalendarView calendarView;
    @FXML
    private Label notificationBadge;
    @FXML
    private Hyperlink handleAfficherCalendrier;

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

                css.setVisible(true);
                handleAfficherOrdonnances.setVisible(true);
                handleAfficherFacture.setVisible(false);
                handlemodifArticles.setVisible(true);
                handleAfficherMatching.setVisible(false);
                handleAfficherPlanification.setVisible(false);
              //  handleMesArticles.setVisible(true);
                handleAfficherCalendrier.setVisible(true);


            }
            else if (CurrentUser.getRole()== EnumRole.ROLE_PHARMACIEN)
            {
                css.setVisible(false);
                handleAfficherOrdonnances.setVisible(true);
                handleAfficherFacture.setVisible(true);
                handlemodifArticles.setVisible(false);
                handleAfficherMatching.setVisible(false);
                handleAfficherPlanification.setVisible(false);
              //  handleMesArticles.setVisible(true);
                handleAfficherCalendrier.setVisible(false);



            }else {
                css.setVisible(false);
                handleAfficherOrdonnances.setVisible(false);
                handleAfficherFacture.setVisible(false);
                handlemodifArticles.setVisible(false);
                handleAfficherMatching.setVisible(true);
                handleAfficherPlanification.setVisible(true);
               // handleMesArticles.setVisible(true);
                handleAfficherCalendrier.setVisible(false);


            }
        } else {
            System.err.println("Aucun utilisateur connecté.");
        }
    }



    @FXML
    private void handleAfficherMatching(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MatchingView.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleAfficherPlanification(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PlanificationView.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void css(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardConsultation.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void handleAfficherCalendrier() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Calendrier.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    private void handlemodifArticles(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ArticleDoctor.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }






    @FXML
    private void handleAfficherOrdonnances(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheOrdonnance.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    private void handleAfficherFacture(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheFacturation.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void Deconnecter (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();

        // Get the current stage (window)
        Stage stage = (Stage) css.getScene().getWindow();

        // Set the new scene with the home.fxml content
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    private void checkUnreadMessages() {
        if (CurrentUser == null) return;

        ServiceMessage serviceMessage = new ServiceMessage();
        int unreadCount = serviceMessage.getUnreadCount(CurrentUser.getId());

        Platform.runLater(() -> {
            boolean shouldShow = unreadCount > 0;
            if (shouldShow && !chatBadge.isVisible()) {
                playPopAnimation(chatBadge); // 🔥 Bounce when new unread arrives
            }
            chatBadge.setVisible(shouldShow);
            chatBadge.setText(shouldShow ? String.valueOf(unreadCount) : "");
        });
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

            chatBadge.setVisible(false); // ✅ When chat opens => reset badge
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleNotificationClick() {
        try {
            ServiceNotification serviceNotification = new ServiceNotification();
            List<Notification> unreadNotifications = serviceNotification.getUnreadForUser(CurrentUser);
            if (unreadNotifications.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Aucune nouvelle notification.");
                alert.showAndWait();
            } else {
                StringBuilder message = new StringBuilder();
                for (Notification n : unreadNotifications) {
                    message.append("- ").append(n.getMessage()).append("\n");
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION, message.toString());
                alert.setHeaderText("Notifications");
                alert.showAndWait();

                // 🔥 Mark all notifications as read
                serviceNotification.markAllAsRead(CurrentUser);

                // 🔥 Hide badge after viewing
                notificationBadge.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAutoRefreshBadge() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {

            checkUnreadMessages();
            checkUnreadNotifications();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    private void checkUnreadNotifications() {
        if (CurrentUser == null) return;

        ServiceNotification serviceNotification = new ServiceNotification();
        try {
            List<Notification> unreadNotifications = serviceNotification.getUnreadForUser(CurrentUser);
            Platform.runLater(() -> {
                boolean shouldShow = !unreadNotifications.isEmpty();
                if (shouldShow && !notificationBadge.isVisible()) {
                    playPopAnimation(notificationBadge); // 🔥 Bounce when new notification arrives
                }
                notificationBadge.setVisible(shouldShow);
                notificationBadge.setText(shouldShow ? String.valueOf(unreadNotifications.size()) : "");
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ✨ Animation pop when badge appears
    private void playPopAnimation(Node node) {
        if (node == null) return;

        ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
        st.setFromX(0);
        st.setFromY(0);
        st.setToX(1);
        st.setToY(1);
        st.play();
    }
    private void playNotificationSound() {
        try {
            AudioClip clip = new AudioClip(getClass().getResource("/sounds/ping.mp3").toExternalForm());
            clip.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void call(MouseEvent event) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI("https://meet.jit.si/ClinicFlow"));
            System.out.println("Jitsi meeting opened in external browser!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
