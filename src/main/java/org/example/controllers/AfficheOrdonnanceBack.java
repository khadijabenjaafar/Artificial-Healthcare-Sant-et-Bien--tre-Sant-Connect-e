package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.entities.Ordonnance;
import org.example.services.ServiceOrdonnance;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AfficheOrdonnanceBack implements Initializable {

    @FXML
    private ScrollPane scrollPane;
    private Stage modalStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            List<Ordonnance> ordonnances = new ServiceOrdonnance().recuperer();
            afficherCarousel(ordonnances);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherCarousel(List<Ordonnance> ordonnances) {
        HBox hbox = new HBox();
        hbox.setSpacing(20);
        hbox.setPadding(new Insets(20));
        hbox.setAlignment(Pos.CENTER_LEFT);

        for (Ordonnance ordonnance : ordonnances) {
            VBox card = createOrdonnanceCard(ordonnance);
            hbox.getChildren().add(card);
        }

        scrollPane.setContent(hbox);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
    }

    private VBox createOrdonnanceCard(Ordonnance ordonnance) {
        // Format personnalisé pour la date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Convertir la LocalDate en String avec le format désiré
        Label dateLabel = new Label("Date : " + ordonnance.getDate().format(formatter));
        dateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label medicamentsLabel = new Label("Médicaments : " + ordonnance.getMedicaments());
        medicamentsLabel.setWrapText(true);

        Label commentaireLabel = new Label("Commentaire : " + ordonnance.getCommantaire());
        commentaireLabel.setWrapText(true);

        Button viewDetailsBtn = new Button("Voir Détails");
        viewDetailsBtn.setStyle("-fx-background-color: #0fb5a7; -fx-text-fill: white;");
        viewDetailsBtn.setOnAction(e -> showDetailsModal(ordonnance));

        VBox card = new VBox(10, dateLabel, medicamentsLabel, commentaireLabel, viewDetailsBtn);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-border-color: #15d5bc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.1, 0, 0);");

        return card;
    }

    private void showDetailsModal(Ordonnance ordonnance) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Label date = new Label("Date : " + ordonnance.getDate().format(formatter));
        Label medicaments = new Label("Médicaments : " + ordonnance.getMedicaments());
        Label commentaire = new Label("Commentaire : " + ordonnance.getCommantaire());
        Label duree = new Label("Durée utilisation : " + ordonnance.getDureeUtilisation());
        Label quantite = new Label("Quantité : " + ordonnance.getQuantiteUtilisation());

        Button modifierBtn = new Button("Modifier");
        modifierBtn.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white;");
        modifierBtn.setOnAction(event -> showModificationForm(ordonnance));

        Button supprimerBtn = new Button("Supprimer");
        supprimerBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
        supprimerBtn.setOnAction(event -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de Suppression");
            confirmationAlert.setHeaderText("Êtes-vous sûr ?");
            confirmationAlert.setContentText("Supprimer cette ordonnance ?");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        ServiceOrdonnance service = new ServiceOrdonnance();
                        service.supprimer(ordonnance.getId());
                        initialize(null, null); // Recharger les données (carousel)

                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Succès");
                        successAlert.setHeaderText("✅ Suppression réussie !");
                        successAlert.setContentText("L'ordonnance a été supprimée avec succès.");
                        successAlert.showAndWait();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });

        HBox buttonsBox = new HBox(10, modifierBtn, supprimerBtn);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox detailContent = new VBox(10, date, medicaments, commentaire, duree, quantite, buttonsBox);
        detailContent.setAlignment(Pos.CENTER_LEFT);
        detailContent.setPadding(new Insets(20));

        // ⬇️ Affichage dans l'interface principale (ScrollPane ou autre conteneur)
        scrollPane.setContent(detailContent);
    }


    private void showModificationForm(Ordonnance ordonnance) {
        // Créer une nouvelle fenêtre pour la modification des informations
        Stage modificationStage = new Stage();
        modificationStage.initModality(Modality.APPLICATION_MODAL);
        modificationStage.setTitle("Modifier Ordonnance");

        // Créer des champs pour la modification
        Label dateLabel = new Label("Date : ");
        TextField dateField = new TextField(ordonnance.getDate().toString());

        Label medicamentsLabel = new Label("Médicaments : ");
        TextField medicamentsField = new TextField(ordonnance.getMedicaments());

        Label commentaireLabel = new Label("Commentaire : ");
        TextField commentaireField = new TextField(ordonnance.getCommantaire());

        Label dureeLabel = new Label("Durée : ");
        TextField dureeField = new TextField(ordonnance.getDureeUtilisation());

        Label quantiteLabel = new Label("Quantité : ");
        TextField quantiteField = new TextField(String.valueOf(ordonnance.getQuantiteUtilisation()));

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white;");
        saveBtn.setOnAction(event -> {
            // Vérifiez si les champs sont modifiés
            boolean isModified = false;

            // Récupérer les nouvelles valeurs et vérifier les modifications
            String newDate = dateField.getText();
            String newMedicaments = medicamentsField.getText();
            String newCommentaire = commentaireField.getText();
            String newDuree = dureeField.getText();
            String newQuantite = quantiteField.getText();

            // Comparer avec les valeurs originales de l'ordonnance
            if (!newDate.equals(ordonnance.getDate().toString())) {
                isModified = true;
                ordonnance.setDate(LocalDate.parse(newDate));  // Parse la date
            }
            if (!newMedicaments.equals(ordonnance.getMedicaments())) {
                isModified = true;
                ordonnance.setMedicaments(newMedicaments);
            }
            if (!newCommentaire.equals(ordonnance.getCommantaire())) {
                isModified = true;
                ordonnance.setCommantaire(newCommentaire);
            }
            if (!newDuree.equals(ordonnance.getDureeUtilisation())) {
                isModified = true;
                ordonnance.setDureeUtilisation(newDuree);
            }

            // Extraire le nombre et le texte de la quantité
            String[] result = extractNumberAndText(newQuantite); // Utilise la méthode pour séparer nombre et texte
            String newQuantiteNombre = result[0]; // Nombre
            String newQuantiteTexte = result[1];  // Texte

            if (!newQuantiteNombre.equals(ordonnance.getQuantiteUtilisation()) || !newQuantiteTexte.equals(ordonnance.getTexteQuantite())) {
                isModified = true;
                ordonnance.setQuantiteUtilisation(newQuantiteNombre); // Met à jour la quantité
                ordonnance.setTexteQuantite(newQuantiteTexte); // Ajoute le texte associé à la quantité
            }

            // Si aucune modification n'est effectuée, affichez un message
            if (!isModified) {
                showConfirmationDialog(modificationStage, "Aucune modification effectuée", "Aucune modification n'a été effectuée sur cette ordonnance.");
            } else {
                try {
                    // Appeler la méthode de service pour mettre à jour l'ordonnance dans la base de données
                    new ServiceOrdonnance().modifier(ordonnance);
                    System.out.println("Ordonnance mise à jour !");

                    // Créer la fenêtre de confirmation pour une modification réussie
                    showConfirmationDialog(modificationStage, "Modification réussie", "L'ordonnance a été modifiée avec succès.");

                    modificationStage.close(); // Ferme la fenêtre de modification
                    modalStage.close(); // Ferme la fenêtre modale
                    initialize(null, null); // Recharger les ordonnances dans le carousel
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        VBox vbox = new VBox(10, dateLabel, dateField, medicamentsLabel, medicamentsField, commentaireLabel, commentaireField, dureeLabel, dureeField, quantiteLabel, quantiteField, saveBtn);
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 400, 350);
        modificationStage.setScene(scene);
        modificationStage.showAndWait();
    }

    private void showConfirmationDialog(Stage parentStage, String title, String message) {
        Stage confirmationStage = new Stage();
        confirmationStage.initModality(Modality.APPLICATION_MODAL);
        confirmationStage.initOwner(parentStage);
        confirmationStage.setTitle(title);

        Label label = new Label(message);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button okButton = new Button("OK");
        okButton.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white;");
        okButton.setOnAction(e -> confirmationStage.close());

        VBox vbox = new VBox(10, label, okButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 300, 150);
        confirmationStage.setScene(scene);
        confirmationStage.showAndWait();
    }

    private String[] extractNumberAndText(String quantite) {
        // Logic for splitting number and text from quantite
        // For example, if "5 units", return ["5", "units"]
        String[] result = new String[2];
        result[0] = quantite.replaceAll("[^0-9]", "");  // Extract number part
        result[1] = quantite.replaceAll("[0-9]", "").trim();  // Extract text part
        return result;
    }
}
