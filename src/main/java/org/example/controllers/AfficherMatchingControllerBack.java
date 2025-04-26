package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.entities.Matching;
import org.example.services.ServiceMatching;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AfficherMatchingControllerBack implements Initializable {

    @FXML private ListView<Matching> matchingListView;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> availabilityFilter;

    private ServiceMatching serviceMatching;
    private ObservableList<Matching> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceMatching = new ServiceMatching();
        setupListView();
        setupSearchAndFilters();
        loadMatchingData();
    }

    private void setupListView() {
        matchingListView.setCellFactory(param -> new ListCell<Matching>() {
            @Override
            protected void updateItem(Matching matching, boolean empty) {
                super.updateItem(matching, empty);

                if (empty || matching == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create main container
                    VBox container = new VBox(8);
                    container.setPadding(new Insets(12));
                    container.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

                    // Top row with basic info
                    HBox topRow = new HBox(15);
                    Label idLabel = new Label("#" + matching.getId());
                    idLabel.setStyle("-fx-font-weight: bold;");

                    Label cinLabel = new Label("CIN: " + matching.getCin());
                    Label dateLabel = new Label(matching.getDate().toString());
                    dateLabel.setStyle("-fx-text-fill: #555555;");

                    Label availabilityLabel = new Label(matching.isAvailability() ? "Available" : "Not Available");
                    availabilityLabel.setTextFill(matching.isAvailability() ? Color.GREEN : Color.RED);

                    topRow.getChildren().addAll(idLabel, cinLabel, dateLabel, availabilityLabel);

                    // Middle section with details
                    VBox detailsBox = new VBox(5);
                    Label descriptionLabel = new Label("Description: " + matching.getDescription());
                    Label competencesLabel = new Label("Skills: " + matching.getCompetences());
                    Label priceLabel = new Label("Price: " + matching.getPrice() + " DT");

                    detailsBox.getChildren().addAll(descriptionLabel, competencesLabel, priceLabel);

                    container.getChildren().addAll(topRow, detailsBox);
                    setGraphic(container);
                }
            }
        });
    }

    private void setupSearchAndFilters() {
        availabilityFilter.getItems().addAll("All", "Available", "Not Available");
        availabilityFilter.setValue("All");

        FilteredList<Matching> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(matching -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return matching.getCin().toLowerCase().contains(lowerCaseFilter) ||
                        matching.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                        matching.getCompetences().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(matching.getPrice()).contains(lowerCaseFilter);
            });
        });

        availabilityFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(matching -> {
                if (newValue.equals("All")) {
                    return true;
                }
                boolean available = newValue.equals("Available");
                return matching.isAvailability() == available;
            });
        });

        matchingListView.setItems(filteredData);
    }

    private void loadMatchingData() {
        try {
            masterData.setAll(serviceMatching.findAll());
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading data: " + e.getMessage());
        }
    }


    @FXML
    private void handleAddMatching() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_matching.fxml"));
            Parent root = loader.load();

            AddMatchingController controller = loader.getController();
            controller.setRefreshCallback(this::loadMatchingData);

            Stage stage = new Stage();
            stage.setTitle("Add New Matching");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Could not load the add matching window: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}