package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import org.example.entities.Notification;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import org.example.services.ServiceNotification;
import java.io.IOException;
import org.example.services.ServiceUtilisateur;

import java.util.List;

public class Doctor {
    public Utilisateur CurrentUser= UserConnecter.getInstance().getUserConnecter();
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
}
