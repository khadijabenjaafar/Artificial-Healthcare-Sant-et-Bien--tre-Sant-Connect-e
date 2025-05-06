package org.example.controllers;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.sql.SQLException;
import java.util.Map;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import com.stripe.model.checkout.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.example.entities.Facturation;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import org.example.services.ServiceFacturation;
import org.example.utils.QRCodeUtil;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import static org.example.services.TwilioService.sendPaymentConfirmation;

public class AfficheFacturationP implements Initializable {

    @FXML
    private ScrollPane scrollPane;
    private Map<String, Facturation> activePaymentSessions = new HashMap<>();
    private Timer paymentCheckTimer;
    public Utilisateur CurrentUser= UserConnecter.getInstance().getUserConnecter();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Facturation> facturations = new ServiceFacturation().afficher();
            afficherCarousel(facturations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherCarousel(List<Facturation> facturations) throws Exception {
        HBox hbox = new HBox(25);
        hbox.setPadding(new Insets(30));
        hbox.setAlignment(Pos.CENTER_LEFT);

        for (Facturation facturation : facturations) {
            VBox card = createFacturationCard(facturation);
            hbox.getChildren().add(card);
        }

        scrollPane.setContent(hbox);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
    }

    private VBox createFacturationCard(Facturation facturation) throws Exception {
        Label title = new Label("Facturation");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#0fb5a7"));

        Label montantLabel = new Label("💰 Montant : " + facturation.getMontant() + " TND");
        Label dateLabel = new Label("📅 Date : " + facturation.getDateFacturation());
        Label methodePLabel = new Label("💳 Méthode : " + facturation.getMethodePaiement());

        // Change the color of status text based on payment status
        Label statutLabel = new Label("📌 Statut : " + facturation.getStatut());
        if ("Payé".equals(facturation.getStatut())) {
            statutLabel.setTextFill(Color.web("#28a745")); // Green color for paid
            statutLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        } else {
            statutLabel.setFont(Font.font("Arial", 13));
        }

        for (Label label : new Label[]{montantLabel, dateLabel, methodePLabel}) {
            label.setFont(Font.font("Arial", 13));
        }

        Button viewDetailsBtn = new Button("👁 Voir Détails");
        viewDetailsBtn.setStyle("-fx-background-color: #0fb5a7; -fx-text-fill: white; -fx-font-weight: bold;");
        viewDetailsBtn.setOnAction(e -> showDetailsModal(facturation));

        // ✅ QR Code
        String data = "Facturation ID: " + facturation.getId() +
                "\nMontant: " + facturation.getMontant() +
                "\nDate: " + facturation.getDateFacturation() +
                "\nMéthode: " + facturation.getMethodePaiement() +
                "\nStatut: " + facturation.getStatut();
        ImageView qrImageView = new ImageView(QRCodeUtil.generateQRCodeImage(data, 100, 100));
        qrImageView.setFitHeight(100);
        qrImageView.setFitWidth(100);

        // ✅ PDF Button
        Button pdfBtn = new Button("🧾 Export PDF");
        pdfBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        pdfBtn.setOnAction(e -> exportPDF(facturation));

        // ✅ Payer Button - Disabled if already paid
        Button payerBtn = new Button();

        if ("Payé".equals(facturation.getStatut())) {
            payerBtn.setText("✅ Déjà Payé");
            payerBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
            payerBtn.setDisable(true);
        } else {
            payerBtn.setText("💳 Payer");
            payerBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
            payerBtn.setOnAction(e -> payerFacturation(facturation));
        }

        VBox card = new VBox(10, title, montantLabel, dateLabel, methodePLabel, statutLabel, qrImageView, viewDetailsBtn, pdfBtn, payerBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setPrefWidth(250);

        // Change card border color based on payment status
        String borderColor = "Payé".equals(facturation.getStatut()) ? "#28a745" : "#15d5bc";

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0.1, 0, 2);"
        );

        card.setOnMouseEntered(e -> {
            String hoverBorderColor = "Payé".equals(facturation.getStatut()) ? "#218838" : "#0fb5a7";
            card.setStyle(
                    "-fx-background-color: #f7fdfd;" +
                            "-fx-border-color: " + hoverBorderColor + ";" +
                            "-fx-border-radius: 15;" +
                            "-fx-background-radius: 15;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0.2, 0, 4);"
            );
        });

        card.setOnMouseExited(e -> {
            String exitBorderColor = "Payé".equals(facturation.getStatut()) ? "#28a745" : "#15d5bc";
            card.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-border-color: " + exitBorderColor + ";" +
                            "-fx-border-radius: 15;" +
                            "-fx-background-radius: 15;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0.1, 0, 2);"
            );
        });

        return card;
    }
    private void showDetailsModal(Facturation facturation) {
        // Création de la fenêtre modale
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Détails Facturation #" + facturation.getId());

        // Conteneur principal
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setStyle("-fx-background-color: #f5f7fa;");

        // Titre avec icône
        HBox titleBox = new HBox(10);
        Label title = new Label("Détails de la Facturation");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Icône de facturation (remplacer par votre propre icône si disponible)
        Label icon = new Label("📋");
        icon.setStyle("-fx-font-size: 24px;");
        titleBox.getChildren().addAll(icon, title);
        titleBox.setAlignment(Pos.CENTER);

        // Carte d'information
        VBox infoCard = new VBox(12);
        infoCard.setPadding(new Insets(20));
        infoCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Style commun
        String labelStyle = "-fx-font-size: 14px; -fx-text-fill: #7f8c8d;";
        String valueStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;";

        // Montant
        HBox amountBox = createDetailRow("💰", "Montant :", facturation.getMontant() + " TND", labelStyle, valueStyle);

        // Date
        HBox dateBox = createDetailRow("📅", "Date :", facturation.getDateFacturation().toString(), labelStyle, valueStyle);

        // Statut
        String statusColor = "Payé".equals(facturation.getStatut()) ? "#27ae60" : "#e74c3c";
        HBox statusBox = createDetailRow("📌", "Statut :", facturation.getStatut(),
                labelStyle, valueStyle + " -fx-text-fill: " + statusColor + ";");

        // Méthode
        HBox methodBox = createDetailRow("💳", "Méthode :", facturation.getMethodePaiement(), labelStyle, valueStyle);

        infoCard.getChildren().addAll(amountBox, dateBox, statusBox, methodBox);

        // Boutons d'action
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);



        // Bouton PDF
        Button pdfBtn = new Button("Exporter PDF");
        pdfBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        pdfBtn.setGraphic(new Label("📄"));
        pdfBtn.setOnAction(e -> {
            exportPDF(facturation);
            modalStage.close();
        });

        // Bouton Fermer
        Button closeBtn = new Button("Fermer");
        closeBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
        closeBtn.setGraphic(new Label("✕"));
        closeBtn.setOnAction(e -> modalStage.close());

        buttonBox.getChildren().addAll( pdfBtn, closeBtn);

        // Assemblage final
        mainContainer.getChildren().addAll(titleBox, infoCard, buttonBox);

        // Configuration de la scène
        Scene scene = new Scene(mainContainer, 400, 350);
        modalStage.setScene(scene);
        modalStage.show();
    }

    // Méthode utilitaire pour créer une ligne de détail
    private HBox createDetailRow(String emoji, String label, String value, String labelStyle, String valueStyle) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 16px;");

        Label labelLbl = new Label(label);
        labelLbl.setStyle(labelStyle);

        Label valueLbl = new Label(value);
        valueLbl.setStyle(valueStyle);

        row.getChildren().addAll(emojiLabel, labelLbl, valueLbl);
        return row;
    }

    private void showModificationForm(Facturation facturation) {
        TextField montantField = new TextField(String.valueOf(facturation.getMontant()));
        TextField methodeField = new TextField(facturation.getMethodePaiement());
        TextField statutField = new TextField(facturation.getStatut());

        Button saveBtn = new Button("💾 Enregistrer");
        saveBtn.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
        saveBtn.setOnAction(event -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Modifier cette facturation ?", ButtonType.OK, ButtonType.CANCEL);
            confirm.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK) {
                    try {
                        facturation.setMontant(Double.parseDouble(montantField.getText()));
                        facturation.setMethodePaiement(methodeField.getText());
                        facturation.setStatut(statutField.getText());
                        new ServiceFacturation().modifier(facturation);
                        showInfo("✅ Modification réussie !");
                        initialize(null, null);
                    } catch (Exception ex) {
                        showError("Erreur : " + ex.getMessage());
                    }
                }
            });
        });

        VBox form = new VBox(10,
                new Label("Montant : "), montantField,
                new Label("Méthode : "), methodeField,
                new Label("Statut : "), statutField,
                saveBtn);
        form.setPadding(new Insets(20));
        form.setAlignment(Pos.CENTER_LEFT);

        scrollPane.setContent(form);
    }

    private void exportPDF(Facturation facturation) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                // Configuration
                float pageWidth = page.getMediaBox().getWidth();
                float marginX = 50;
                float yPosition = 750;
                float lineHeight = 20;

                // 1. EN-TÊTE FIXE ============================================
                // Logo
                try {
                    PDImageXObject logo = PDImageXObject.createFromFileByExtension(
                            new File("C:/Artificial-Healthcare-Sant-et-Bien--tre-Sant-Connect-e/src/main/resources/img/logo.png"), document);
                    content.drawImage(logo, marginX, yPosition - 40, 100, 50);
                } catch (IOException e) {
                    content.beginText();
                    content.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    content.newLineAtOffset(marginX, yPosition - 25);
                    content.showText("CLINIQUE MEDICALE");
                    content.endText();
                }

                // Titre principal
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 18);
                content.setNonStrokingColor(0, 102, 153);
                content.newLineAtOffset(pageWidth/2 - 60, yPosition + 10);
                content.showText("FACTURE N° " + facturation.getId());
                content.endText();

                // Informations société
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 10);
                content.newLineAtOffset(pageWidth - 200, yPosition);
                content.showText("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                content.newLineAtOffset(0, -lineHeight);
                content.showText("SIRET: 123 456 789 00012");
                content.endText();

                // 2. DESTINATAIRE ===========================================
                float destY = yPosition - 70;
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.newLineAtOffset(marginX, destY);
                content.showText("Destinataire:");
                content.setFont(PDType1Font.HELVETICA, 11);
                content.newLineAtOffset(0, -lineHeight * 1.2f);
                content.showText("Patient/Caisse d'Assurance Maladie");
                content.newLineAtOffset(0, -lineHeight * 1.2f);
                content.showText("Facturation Sécurisée Santé");
                content.endText();

                // 3. TABLEAU CENTRÉ AGRANDI ==================================
                float tableWidth = 500;
                float tableX = (pageWidth - tableWidth) / 2;
                float tableY = yPosition - 180;

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String dateFormatted = facturation.getDateFacturation().format(formatter);

                String[][] tableData = {
                        {"Date de facturation", dateFormatted},
                        {"Montant total", String.format("%.2f €", facturation.getMontant())},
                        {"Mode de règlement", facturation.getMethodePaiement()},
                        {"Statut du paiement", facturation.getStatut()}
                };

                float finalTableY = drawLargeCenteredTable(content, tableX, tableY, tableWidth, tableData);

                // 4. SECTION SIGNATURE =======================================
                float signatureY = finalTableY - 40;

                // Ligne de signature
                content.setStrokingColor(150, 150, 150);
                content.setLineWidth(1f);
                content.moveTo(tableX + 150, signatureY);
                content.lineTo(tableX + 350, signatureY);
                content.stroke();

                // Texte signature
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                content.setNonStrokingColor(100, 100, 100);
                content.newLineAtOffset(tableX + 200, signatureY - 15);
                content.showText("Le responsable administratif");
                content.endText();

                // Cachet médical (optionnel)
                try {
                    PDImageXObject stamp = PDImageXObject.createFromFileByExtension(
                            new File("\"C:/Artificial-Healthcare-Sant-et-Bien--tre-Sant-Connect-e/src/main/resources/img/singature.png"), document);
                    content.drawImage(stamp, tableX + 360, signatureY - 40, 60, 30);
                } catch (IOException e) {
                    // Fallback si pas de cachet
                }

                // 5. PIED DE PAGE FIXE ======================================
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                content.newLineAtOffset(marginX, 50);
                content.showText("IBAN: FR76 1234 5678 9123 4567 8901 234 • BIC: CMBPFRPP123");
                content.newLineAtOffset(0, -lineHeight);
                content.showText("TVA Intracom: FR 12 123456789 • www.clinique-medicale.fr • Tél: 01 23 45 67 89");
                content.endText();

            }

            // Sauvegarde
            String fileName = "Facture_Medicale_" + facturation.getId() + ".pdf";
            document.save(new File(fileName));
            showInfo("Facture générée: " + fileName);

        } catch (IOException e) {
            showError("Erreur génération PDF: " + e.getMessage());
        }
    }

    private float drawLargeCenteredTable(PDPageContentStream content, float x, float y,
                                         float totalWidth, String[][] data) throws IOException {
        float[] columnWidths = {totalWidth * 0.45f, totalWidth * 0.55f};

        // En-tête du tableau
        content.setNonStrokingColor(240, 240, 240);
        content.addRect(x, y - 30, totalWidth, 30);
        content.fill();

        // Titre du tableau
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 12);
        content.setNonStrokingColor(0, 0, 0);
        content.newLineAtOffset(x + 15, y - 20);
        content.showText("DÉTAILS DE LA FACTURE");
        content.endText();

        // Lignes de données
        float currentY = y - 60;
        for (String[] row : data) {
            // Libellé
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 11);
            content.newLineAtOffset(x + 15, currentY);
            content.showText(row[0]);

            // Valeur
            content.setFont(PDType1Font.HELVETICA, 11);
            content.newLineAtOffset(columnWidths[0], 0);
            content.showText(row[1]);
            content.endText();

            // Ligne séparatrice
            content.setStrokingColor(180, 180, 180);
            content.setLineWidth(0.8f);
            content.moveTo(x, currentY - 15);
            content.lineTo(x + totalWidth, currentY - 15);
            content.stroke();

            currentY -= 35;
        }

        return currentY - 15;
    }


    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }


    private void payerFacturation(Facturation facturation) {
        try {
            // Configuration de Stripe (côté serveur)
            Stripe.apiKey = "sk_test_51RHazaDI0LqalPUkTJN7gxNC8QBqG2a0M0wDEEC6fvhKX9igeogeef0OReZ29d0idNX2is0ZizvJ6gmbPS7VT2UG00P4rXo9hx";

            // Créer une session de paiement
            Map<String, Object> params = new HashMap<>();
            params.put("payment_method_types", Arrays.asList("card"));
            params.put("line_items", Arrays.asList(
                    new HashMap<String, Object>() {{
                        put("price_data", new HashMap<String, Object>() {{
                            put("currency", "eur");
                            put("product_data", new HashMap<String, Object>() {{
                                put("name", "Facturation #" + facturation.getId());
                            }});
                            put("unit_amount", (int)(facturation.getMontant() * 100));
                        }});
                        put("quantity", 1);
                    }}
            ));
            params.put("mode", "payment");
            params.put("success_url", "https://example.com/success?session_id={CHECKOUT_SESSION_ID}");
            params.put("cancel_url", "https://example.com/cancel");

            Session session = Session.create(params);

            // Store the session ID and facturation for reference
            activePaymentSessions.put(session.getId(), facturation);

            // Open the URL in the browser
            Platform.runLater(() -> {
                try {
                    java.awt.Desktop.getDesktop().browse(new URI(session.getUrl()));

                    // Start polling for payment status
                    startPaymentStatusPolling(session.getId(), facturation);

                } catch (Exception e) {
                    showError("Erreur d'ouverture du navigateur: " + e.getMessage());
                }
            });

        } catch (StripeException e) {
            showError("Erreur Stripe : " + e.getMessage());
        }
    }

    private boolean validateForm(String cardNumber, String expDate, String cvc) {
        if (cardNumber.isEmpty() || expDate.isEmpty() || cvc.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return false;
        }

        if (!cardNumber.matches("^\\d{16}$") && !cardNumber.matches("^\\d{4} \\d{4} \\d{4} \\d{4}$")) {
            showError("Numéro de carte invalide");
            return false;
        }

        if (!expDate.matches("^(0[1-9]|1[0-2])\\/(\\d{2})$")) {
            showError("Format de date invalide (MM/AA)");
            return false;
        }

        if (!cvc.matches("^\\d{3,4}$")) {
            showError("Code CVC invalide");
            return false;
        }

        return true;
    }

    private void processRealPayment(Facturation facturation, String cardNumber, String expDate, String cvc)
            throws StripeException, SQLException {

        // Configuration de Stripe
        Stripe.apiKey = "sk_test_51RGjEt03F1u89IBP849cjWDLt6HKQGJOsH6qhoY6MMoVY2r6SqzyBZTNfQqkLnhfgdnp1XbY6ZQIgHREN95b0X4t00tKdJtYlc";

        // Création du token de carte
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("number", cardNumber);

        String[] expParts = expDate.split("/");
        cardParams.put("exp_month", expParts[0]);
        cardParams.put("exp_year", "20" + expParts[1]);
        cardParams.put("cvc", cvc);

        Map<String, Object> tokenParams = new HashMap<>();
        tokenParams.put("card", cardParams);

        // Création du token
        Token token = Token.create(tokenParams);

        // Création de la charge
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (int) (facturation.getMontant() * 100));
        chargeParams.put("currency", "eur");
        chargeParams.put("description", "Facture #" + facturation.getId());
        chargeParams.put("source", token.getId());

        // Exécution du paiement
        Charge charge = Charge.create(chargeParams);

        // Mise à jour de la facture
        facturation.setStatut("Payé");
        new ServiceFacturation().modifier(facturation);
        System.out.println(CurrentUser.getnumTel());
        sendPaymentConfirmation(CurrentUser.getnumTel(),facturation.getMontant(), facturation.getId());
        System.out.println("aaaaaaaaaaa");

        showInfo("✅ Paiement réussi!\nID de transaction: " + charge.getId());
        initialize(null, null);

    }
    private void startPaymentStatusPolling(String sessionId, Facturation facturation) {
        if (paymentCheckTimer != null) {
            paymentCheckTimer.cancel();
        }

        paymentCheckTimer = new Timer();
        paymentCheckTimer.schedule(new TimerTask() {
            int attempts = 0;

            @Override
            public void run() {
                if (attempts > 60) { // Increased from 30 to 60 attempts
                    paymentCheckTimer.cancel();
                    Platform.runLater(() -> showError("Le délai d'attente pour la vérification du paiement a expiré."));
                    return;
                }

                try {
                    // Retrieve the full session object
                    Session session = Session.retrieve(sessionId);

                    // Check the payment status - look for "paid" or "complete" status
                    if (session != null && "complete".equals(session.getStatus())) {
                        System.out.println("Payment completed successfully!"+
                                "\nSession ID: " + session.getId() +
                                "\nPayment Status: " + session.getPaymentStatus());
                        paymentCheckTimer.cancel();

                        Platform.runLater(() -> {
                            try {
                                // Update facturation status
                                facturation.setStatut("Payé");
                                sendPaymentConfirmation(CurrentUser.getnumTel(),facturation.getMontant(), facturation.getId());
                                new ServiceFacturation().modifier(facturation);

                                // Show success interface
                                showSuccessInterface(facturation);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                } catch (StripeException e) {
                    e.printStackTrace();
                }

                attempts++;
            }
        }, 3000, 2000); // Increased initial delay to 3 seconds and polling interval to 2 seconds
    }
    private void showSuccessInterface(Facturation facturation) {
        try {
            // Update facturation status
            facturation.setStatut("Payé");
            new ServiceFacturation().modifier(facturation);

            // Create success UI
            VBox successBox = new VBox(20);
            successBox.setAlignment(Pos.CENTER);
            successBox.setPadding(new Insets(30));
            successBox.setStyle("-fx-background-color: white;");

            // Success icon - use a text symbol instead of loading an image
            Label iconLabel = new Label("✅");
            iconLabel.setStyle("-fx-font-size: 64px;");
            successBox.getChildren().add(iconLabel);

            // Success message
            Label titleLabel = new Label("Paiement Réussi !");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            titleLabel.setTextFill(Color.web("#28a745"));

            Label detailsLabel = new Label("Votre paiement de " + facturation.getMontant() +
                    " TND pour la facturation #" + facturation.getId() +
                    " a été effectué avec succès.");
            detailsLabel.setFont(Font.font("Arial", 14));
            detailsLabel.setWrapText(true);
            detailsLabel.setMaxWidth(500);

            // Return button
            Button returnButton = new Button("Retourner à la liste des facturations");
            returnButton.setStyle("-fx-background-color: #0fb5a7; -fx-text-fill: white; -fx-font-size: 14px;");
            returnButton.setOnAction(event -> {
                try {
                    initialize(null, null); // Return to main view
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // Add all elements to the success box
            successBox.getChildren().addAll(titleLabel, detailsLabel, returnButton);

            // Show the success interface
            scrollPane.setContent(successBox);

        } catch (SQLException e) {
            showError("Erreur lors de la mise à jour du statut: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
