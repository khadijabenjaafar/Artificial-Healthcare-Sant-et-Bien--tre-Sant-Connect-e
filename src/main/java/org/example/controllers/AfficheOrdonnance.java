package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.example.entities.Ordonnance;
import org.example.services.ServiceOrdonnance;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AfficheOrdonnance implements Initializable {

    private static final String MAIN_COLOR = "#10D2A0";
    private static final String LIGHT_BG = "#F5FDFA";
    private static final String CARD_BG = "#FFFFFF";

    @FXML
    private ScrollPane scrollPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setStyle("-fx-background:" + LIGHT_BG + ";");
        loadOrdonnances();

    }

    private void loadOrdonnances() {
        try {
            List<Ordonnance> ordonnances = new ServiceOrdonnance().recuperer();
            displayOrdonnances(ordonnances);
        } catch (SQLException e) {
            showAlert("Erreur", "Échec du chargement", Alert.AlertType.ERROR);
        }
    }

    private void displayOrdonnances(List<Ordonnance> ordonnances) {
        FlowPane container = new FlowPane();
        container.setPadding(new Insets(20));
        container.setHgap(20);
        container.setVgap(20);
        container.setAlignment(Pos.TOP_CENTER);

        for (Ordonnance ordonnance : ordonnances) {
            container.getChildren().add(createOrdonnanceCard(ordonnance));
        }

        scrollPane.setContent(container);
        scrollPane.setFitToWidth(true);
    }

    private VBox createOrdonnanceCard(Ordonnance ordonnance) {
        VBox card = new VBox();
        card.setSpacing(0);
        card.setStyle("-fx-background-color: " + CARD_BG + "; " +
                "-fx-border-color: " + MAIN_COLOR + "; " +
                "-fx-border-width: 1px; -fx-border-radius: 10px; " +
                "-fx-background-radius: 10px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Header
        Label header = new Label("ORDONNANCE #" + ordonnance.getId());
        header.setStyle("-fx-background-color: " + MAIN_COLOR + "; " +
                "-fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 14px; -fx-padding: 10px; " +
                "-fx-border-radius: 10px 10px 0 0; -fx-background-radius: 10px 10px 0 0;");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        // Content
        GridPane content = new GridPane();
        content.setPadding(new Insets(15));
        content.setHgap(10);
        content.setVgap(8);

        // Date
        content.add(createStyledLabel("Date:", true), 0, 0);
        content.add(createStyledLabel(ordonnance.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), false), 1, 0);

        // Medications
        content.add(createStyledLabel("Médicaments:", true), 0, 1);
        TextArea medsArea = new TextArea(ordonnance.getMedicaments());
        setupTextArea(medsArea, 3);
        content.add(medsArea, 0, 2, 2, 1);

        // Duration and Quantity
        content.add(createStyledLabel("Durée:", true), 0, 3);
        content.add(createStyledLabel(ordonnance.getDureeUtilisation(), false), 1, 3);
        content.add(createStyledLabel("Quantité:", true), 0, 4);
        content.add(createStyledLabel(ordonnance.getQuantiteUtilisation(), false), 1, 4);

        // Comment
        content.add(createStyledLabel("Commentaire:", true), 0, 5);
        TextArea commentArea = new TextArea(ordonnance.getCommantaire());
        setupTextArea(commentArea, 5);
        content.add(commentArea, 0, 6, 2, 1);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15, 15, 10, 15));

        Button editBtn = createActionButton("Modifier");
        editBtn.setOnAction(e -> editOrdonnance(ordonnance));

        Button pdfBtn = createActionButton("PDF");
        pdfBtn.setOnAction(e -> generatePDF(ordonnance));

        Button deleteBtn = createActionButton("Supprimer");
        deleteBtn.setOnAction(e -> deleteOrdonnance(ordonnance));

        buttonBox.getChildren().addAll(editBtn, pdfBtn, deleteBtn);
        card.getChildren().addAll(header, content, buttonBox);
        card.setPrefWidth(400);

        return card;
    }

    private void editOrdonnance(Ordonnance ordonnance) {
        Stage editWindow = new Stage();
        editWindow.initModality(Modality.APPLICATION_MODAL);
        editWindow.setTitle("Modifier Ordonnance #" + ordonnance.getId());

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Form
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);

        // Medications
        form.add(new Label("Médicaments:"), 0, 0);
        TextField medsField = new TextField(ordonnance.getMedicaments());
        medsField.setPrefWidth(250);
        form.add(medsField, 1, 0);

        // Duration
        form.add(new Label("Durée:"), 0, 1);
        TextField durationField = new TextField(ordonnance.getDureeUtilisation());
        form.add(durationField, 1, 1);

        // Quantity
        form.add(new Label("Quantité:"), 0, 2);
        TextField quantityField = new TextField(ordonnance.getQuantiteUtilisation());
        form.add(quantityField, 1, 2);

        // Comment
        form.add(new Label("Commentaire:"), 0, 3);
        TextArea commentArea = new TextArea(ordonnance.getCommantaire());
        commentArea.setPrefRowCount(3);
        commentArea.setPrefWidth(250);
        form.add(commentArea, 1, 3);

        // Action buttons
        HBox buttons = new HBox(15);
        Button saveBtn = createActionButton("Enregistrer");
        saveBtn.setOnAction(e -> {
            try {
                ordonnance.setMedicaments(medsField.getText());
                ordonnance.setDureeUtilisation(durationField.getText());
                ordonnance.setQuantiteUtilisation(quantityField.getText());
                ordonnance.setCommantaire(commentArea.getText());

                new ServiceOrdonnance().modifier(ordonnance);
                loadOrdonnances();
                editWindow.close();
                showAlert("Succès", "Modification enregistrée", Alert.AlertType.INFORMATION);
            } catch (SQLException ex) {
                showAlert("Erreur", "Échec de la modification", Alert.AlertType.ERROR);
            }
        });

        Button cancelBtn = createActionButton("Annuler");
        cancelBtn.setOnAction(e -> editWindow.close());

        buttons.getChildren().addAll(saveBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(form, buttons);
        Scene scene = new Scene(root, 450, 350);
        editWindow.setScene(scene);
        editWindow.showAndWait();
    }

    private void generatePDF(Ordonnance ordonnance) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.setInitialFileName("Ordonnance_" + ordonnance.getId() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File outputFile = fileChooser.showSaveDialog(scrollPane.getScene().getWindow());
        if (outputFile == null) return;

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                // Configuration
                float margin = 50;
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();
                float y = pageHeight - margin;

                // Police Helvetica
                PDType1Font fontBold = PDType1Font.HELVETICA_BOLD;
                PDType1Font fontNormal = PDType1Font.HELVETICA;
                PDType1Font fontItalic = PDType1Font.HELVETICA_OBLIQUE;

                // En-tête fixe
                content.setNonStrokingColor(16, 210, 160); // RGB for #10D2A0
                content.addRect(0, y - 30, pageWidth, 80);
                content.fill();

                // Logo et info cabinet
                // Logo et info cabinet
                try {
                    PDImageXObject logo = PDImageXObject.createFromFileByExtension(
                            new File("C:/Users/maysa/Artificial-Healthcare-Sant-et-Bien--tre-Sant-Connect-e/src/main/resources/img/logo.png"), document);
                    content.drawImage(logo, margin+450, y - 20, 50, 50);
                } catch (IOException e) {
                    content.beginText();
                    content.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    content.newLineAtOffset(margin, y - 25);
                    content.showText("CLINIQUE MEDICALE");
                    content.endText();
                }
                content.beginText();
                content.setFont(fontBold, 16);
                content.setNonStrokingColor(255, 255, 255);
                content.newLineAtOffset(margin, y);
                content.showText("CABINET MÉDICAL DR. DUPONT");
                content.endText();

                content.beginText();
                content.setFont(fontNormal, 10);
                content.newLineAtOffset(margin, y - 15);
                content.showText("123 Rue de la Santé, 75000 Paris | Tél: 01 23 45 67 89");
                content.endText();

                // Titre document
                content.beginText();
                content.setFont(fontBold, 18);
                content.setNonStrokingColor(0, 0, 0);
                content.newLineAtOffset(pageWidth / 2 - 100, y - 60);
                content.showText("ORDONNANCE MÉDICALE");
                content.endText();

                // Numéro ordonnance
                content.beginText();
                content.setFont(fontBold, 12);
                content.newLineAtOffset(pageWidth - margin - 100, y - 20);
                content.showText("N°: " + ordonnance.getId());
                content.endText();

                y -= 90;

                // Informations patient
                content.beginText();
                content.setFont(fontBold, 12);
                content.newLineAtOffset(margin, y);
                content.showText("Patient: Mayssa Guesmi");
                content.endText();

                content.beginText();
                content.setFont(fontNormal, 10);
                content.newLineAtOffset(margin + 150, y);
                content.showText("Date: " + ordonnance.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                content.endText();

                y -= 30;

                // Ligne de séparation
                content.moveTo(margin, y);
                content.lineTo(pageWidth - margin, y);
                content.stroke();
                y -= 20;

                // Médicaments
                content.beginText();
                content.setFont(fontBold, 14);
                content.newLineAtOffset(margin, y);
                content.showText("PRESCRIPTION:");
                content.endText();
                y -= 25;

                String[] medicaments = ordonnance.getMedicaments().split("\n");
                for (String med : medicaments) {
                    if (!med.trim().isEmpty()) {
                        content.beginText();
                        content.setFont(fontBold, 12);
                        content.newLineAtOffset(margin + 10, y);
                        content.showText("• " + med.trim());
                        content.endText();

                        content.beginText();
                        content.setFont(fontNormal, 10);
                        content.newLineAtOffset(margin + 30, y - 15);
                        content.showText("Durée: " + ordonnance.getDureeUtilisation());
                        content.newLineAtOffset(150, 0);
                        content.showText("Quantité: " + ordonnance.getQuantiteUtilisation());
                        content.endText();

                        y -= 30;
                    }
                }

                y -= 20;

                // Commentaire
                content.beginText();
                content.setFont(fontBold, 14);
                content.newLineAtOffset(margin, y);
                content.showText("OBSERVATIONS:");
                content.endText();
                y -= 20;

                String[] commentLines = ordonnance.getCommantaire().split("\n");
                for (String line : commentLines) {
                    if (!line.trim().isEmpty()) {
                        content.beginText();
                        content.setFont(fontNormal, 12);
                        content.newLineAtOffset(margin + 10, y);
                        content.showText(line.trim());
                        content.endText();
                        y -= 15;
                    }
                }

                y -= 40;

                // Signature
                content.setLineWidth(1);
                content.moveTo(pageWidth - margin - 150, y);
                content.lineTo(pageWidth - margin, y);
                content.stroke();

                content.beginText();
                content.setFont(fontItalic, 10);
                content.newLineAtOffset(pageWidth - margin - 100, y - 15);
                content.showText("Signature et cachet du médecin");
                content.endText();

                // Pied de page fixe
                content.beginText();
                content.setFont(fontNormal, 8);
                content.newLineAtOffset(margin, 30);
                content.showText("Document établi électroniquement - valable sans signature");
                content.endText();

                content.beginText();
                content.setFont(fontNormal, 8);
                content.newLineAtOffset(pageWidth - margin - 150, 30);
                content.showText("Page 1/1");
                content.endText();
            }

            document.save(outputFile);
            showAlert("Succès", "PDF généré avec succès", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Erreur", "Échec de la génération PDF", Alert.AlertType.ERROR);
        }
    }

    private void deleteOrdonnance(Ordonnance ordonnance) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'ordonnance #" + ordonnance.getId() + "?");
        confirm.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                new ServiceOrdonnance().supprimer(ordonnance.getId());
                loadOrdonnances();
                showAlert("Succès", "Ordonnance supprimée", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Échec de la suppression", Alert.AlertType.ERROR);
            }
        }
    }

    private Label createStyledLabel(String text, boolean isTitle) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + (isTitle ? MAIN_COLOR : "#333333") + "; " +
                (isTitle ? "-fx-font-weight: bold;" : ""));
        return label;
    }

    private Button createActionButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + MAIN_COLOR + "; " +
                "-fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 8 15 8 15; -fx-background-radius: 5px;");
        return btn;
    }

    private void setupTextArea(TextArea area, int rows) {
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefRowCount(rows);
        area.setStyle("-fx-control-inner-background: #FAFAFA; " +
                "-fx-border-color: #E0E0E0; -fx-border-radius: 5px;");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    void AjouterO(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutOrdonnance.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) scrollPane.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}