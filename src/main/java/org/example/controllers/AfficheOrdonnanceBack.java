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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AfficheOrdonnanceBack implements Initializable {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private ComboBox<String> sortComboBox;

    private Stage modalStage;
    private List<Ordonnance> allOrdonnances;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUIComponents();
        loadOrdonnances();
    }

    private void setupUIComponents() {
        // Initialisation des combobox pour le tri et le filtre
        filterComboBox.getItems().addAll("Toutes", "Aujourd'hui", "Cette semaine", "Ce mois");
        filterComboBox.setValue("Toutes");

        sortComboBox.getItems().addAll("Date (récent)", "Date (ancien)", "Médicaments (A-Z)", "Médicaments (Z-A)");
        sortComboBox.setValue("Date (récent)");

        // Écouteurs pour les changements
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterOrdonnances());
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterOrdonnances());
        sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterOrdonnances());
    }

    private void loadOrdonnances() {
        try {
            allOrdonnances = new ServiceOrdonnance().recuperer();
            filterOrdonnances();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des ordonnances", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void filterOrdonnances() {
        if (allOrdonnances == null) return;

        // Filtrage
        List<Ordonnance> filtered = allOrdonnances.stream()
                .filter(createSearchPredicate())
                .filter(createDateFilterPredicate())
                .collect(Collectors.toList());

        // Tri
        sortOrdonnances(filtered);

        // Affichage
        afficherCarousel(filtered);
    }

    private Predicate<Ordonnance> createSearchPredicate() {
        String searchText = searchField.getText().toLowerCase();
        return ordonnance ->
                ordonnance.getMedicaments().toLowerCase().contains(searchText) ||
                        ordonnance.getCommantaire().toLowerCase().contains(searchText) ||
                        ordonnance.getDureeUtilisation().toLowerCase().contains(searchText);
    }

    private Predicate<Ordonnance> createDateFilterPredicate() {
        String filterValue = filterComboBox.getValue();
        LocalDate now = LocalDate.now();

        return ordonnance -> {
            if (filterValue == null || filterValue.equals("Toutes")) return true;

            LocalDate date = ordonnance.getDate();
            switch (filterValue) {
                case "Aujourd'hui":
                    return date.isEqual(now);
                case "Cette semaine":
                    return date.isAfter(now.minusDays(7));
                case "Ce mois":
                    return date.isAfter(now.minusMonths(1));
                default:
                    return true;
            }
        };
    }

    private void sortOrdonnances(List<Ordonnance> ordonnances) {
        String sortValue = sortComboBox.getValue();
        if (sortValue == null) return;

        switch (sortValue) {
            case "Date (récent)":
                ordonnances.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                break;
            case "Date (ancien)":
                ordonnances.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
                break;
            case "Médicaments (A-Z)":
                ordonnances.sort((o1, o2) -> o1.getMedicaments().compareToIgnoreCase(o2.getMedicaments()));
                break;
            case "Médicaments (Z-A)":
                ordonnances.sort((o1, o2) -> o2.getMedicaments().compareToIgnoreCase(o1.getMedicaments()));
                break;
        }
    }

    private void afficherCarousel(List<Ordonnance> ordonnances) {
        HBox hbox = new HBox();
        hbox.setSpacing(20);
        hbox.setPadding(new Insets(20));
        hbox.setAlignment(Pos.CENTER_LEFT);

        if (ordonnances.isEmpty()) {
            Label noResults = new Label("Aucune ordonnance trouvée");
            noResults.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            hbox.getChildren().add(noResults);
        } else {
            for (Ordonnance ordonnance : ordonnances) {
                VBox card = createOrdonnanceCard(ordonnance);
                hbox.getChildren().add(card);
            }
        }

        scrollPane.setContent(hbox);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
    }

    private VBox createOrdonnanceCard(Ordonnance ordonnance) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
                        loadOrdonnances(); // Recharger les données après suppression

                        showAlert("Succès", "✅ Suppression réussie !", Alert.AlertType.INFORMATION);
                    } catch (SQLException ex) {
                        showAlert("Erreur", "Erreur lors de la suppression", Alert.AlertType.ERROR);
                        ex.printStackTrace();
                    }
                }
            });
        });

        Button backBtn = new Button("Retour");
        backBtn.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white;");
        backBtn.setOnAction(e -> filterOrdonnances());

        HBox buttonsBox = new HBox(10, modifierBtn, supprimerBtn, backBtn);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox detailContent = new VBox(10, date, medicaments, commentaire, duree, quantite, buttonsBox);
        detailContent.setAlignment(Pos.CENTER_LEFT);
        detailContent.setPadding(new Insets(20));

        scrollPane.setContent(detailContent);
    }

    private void showModificationForm(Ordonnance ordonnance) {
        Stage modificationStage = new Stage();
        modificationStage.initModality(Modality.APPLICATION_MODAL);
        modificationStage.setTitle("Modifier Ordonnance");

        DatePicker datePicker = new DatePicker(ordonnance.getDate());
        TextField medicamentsField = new TextField(ordonnance.getMedicaments());
        TextField commentaireField = new TextField(ordonnance.getCommantaire());
        TextField dureeField = new TextField(ordonnance.getDureeUtilisation());
        TextField quantiteField = new TextField(ordonnance.getQuantiteUtilisation());

        Button saveBtn = new Button("Sauvegarder");
        saveBtn.setOnAction(event -> {
            ordonnance.setDate(datePicker.getValue());
            ordonnance.setMedicaments(medicamentsField.getText());
            ordonnance.setCommantaire(commentaireField.getText());
            ordonnance.setDureeUtilisation(dureeField.getText());
            ordonnance.setQuantiteUtilisation(quantiteField.getText());

            try {
                ServiceOrdonnance service = new ServiceOrdonnance();
                service.modifier(ordonnance);
                showAlert("Succès", "✅ Modification réussie !", Alert.AlertType.INFORMATION);
                loadOrdonnances(); // Recharger les données après modification
                modificationStage.close();
            } catch (SQLException ex) {
                showAlert("Erreur", "Erreur lors de la modification", Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });

        VBox modificationForm = new VBox(10, datePicker, medicamentsField, commentaireField, dureeField, quantiteField, saveBtn);
        modificationForm.setPadding(new Insets(20));

        Scene modificationScene = new Scene(modificationForm, 400, 350);
        modificationStage.setScene(modificationScene);
        modificationStage.show();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
