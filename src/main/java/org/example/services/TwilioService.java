package org.example.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.example.config.TwilioConfig;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TwilioService {
    static {
        Twilio.init(TwilioConfig.ACCOUNT_SID, TwilioConfig.AUTH_TOKEN);
    }

    public static void sendPaymentConfirmation(String toPhoneNumber, double amount, int factureId) {
        String message = String.format(
                "[Clinique Medicale]\n" +
                        "Paiement confirmé: %.2f TND\n" +
                        "Ref: FAC-%d\n" +
                        "Date: %s\n" +
                        "Merci!",
                amount,
                factureId,
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );

        try {
            Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(TwilioConfig.TWILIO_PHONE),
                    message
            ).create();

            // Afficher l'alerte JavaFX après l'envoi réussi
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation SMS");
                alert.setHeaderText(null);
                alert.setContentText("Le SMS de confirmation a été envoyé avec succès !");
                alert.showAndWait();
            });

        } catch (Exception e) {
            // En cas d'erreur d'envoi
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur SMS");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de l'envoi du SMS : " + e.getMessage());
                alert.showAndWait();
            });
        }
    }
}