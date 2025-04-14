package org.example.controllers;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import org.example.entities.Planification;
import org.example.entities.Utilisateur;
import org.example.services.ServicesPlanification;
import org.example.services.ServiceUtilisateur;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class PlanificationController implements Initializable {

    @FXML private ListView<Planification> planificationListView;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    private ServicesPlanification servicesPlanification;
    private ObservableList<Planification> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        servicesPlanification = new ServicesPlanification();
        setupListView();
        setupSearchAndFilters();
        loadPlanificationData();
    }

    private void setupListView() {
        planificationListView.setCellFactory(param -> new ListCell<Planification>() {
            @Override
            protected void updateItem(Planification planification, boolean empty) {
                super.updateItem(planification, empty);

                if (empty || planification == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create main container
                    VBox container = new VBox(8);
                    container.setPadding(new Insets(12));
                    container.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

                    // Top row with basic info
                    HBox topRow = new HBox(15);
                    Label idLabel = new Label("#" + planification.getId());
                    idLabel.setStyle("-fx-font-weight: bold;");

                    Label dateLabel = new Label(planification.getDate().toString());
                    dateLabel.setStyle("-fx-text-fill: #555555;");

                    Label statusLabel = new Label(planification.getStatut());
                    statusLabel.setTextFill(getStatusColor(planification.getStatut()));
                    statusLabel.setStyle("-fx-font-weight: bold;");

                    topRow.getChildren().addAll(idLabel, dateLabel, statusLabel);

                    // Details section
                    VBox detailsBox = new VBox(5);
                    Label modeLabel = new Label("Mode: " + planification.getMode());
                    Label addressLabel = new Label("Address: " + planification.getAdresse());

                    // Freelancer and User info
                    HBox peopleBox = new HBox(15);
                    Utilisateur freelancer = planification.getFreelancer();
                    Utilisateur user = planification.getUtilisateur();

                    Label freelancerLabel = new Label("Freelancer: " +
                            (freelancer != null ? freelancer.getPrenom() + " " + freelancer.getNom() : "N/A"));
                    Label userLabel = new Label("User: " +
                            (user != null ? user.getPrenom() + " " + user.getNom() : "N/A"));

                    peopleBox.getChildren().addAll(freelancerLabel, userLabel);

                    detailsBox.getChildren().addAll(modeLabel, addressLabel, peopleBox);

                    // Action buttons
                    HBox actionBox = new HBox(10);
                    Button editBtn = new Button("Edit");
                    Button deleteBtn = new Button("Delete");
                    Button confirmBtn = new Button("Confirm");
                    Button cancelBtn = new Button("Cancel");

                    // Style buttons
                    editBtn.getStyleClass().add("edit-button");
                    deleteBtn.getStyleClass().add("delete-button");
                    confirmBtn.getStyleClass().add("confirm-button");
                    cancelBtn.getStyleClass().add("cancel-button");

                    // Button actions
                    editBtn.setOnAction(e -> handleEditPlanification(planification));
                    deleteBtn.setOnAction(e -> handleDeletePlanification(planification));
                    confirmBtn.setOnAction(e -> updatePlanificationStatus(planification, "confirmée"));
                    cancelBtn.setOnAction(e -> updatePlanificationStatus(planification, "annulée"));

                    // Only show status buttons for pending status
                    if ("en attente".equals(planification.getStatut())) {
                        actionBox.getChildren().addAll(editBtn, deleteBtn, confirmBtn, cancelBtn);
                    } else {
                        actionBox.getChildren().addAll(editBtn, deleteBtn);
                    }

                    container.getChildren().addAll(topRow, detailsBox, actionBox);
                    setGraphic(container);
                }
            }
        });
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "confirmée": return Color.web("#4CAF50");
            case "annulée": return Color.web("#f44336");
            default: return Color.web("#2196F3");
        }
    }


    private void setupSearchAndFilters() {
        statusFilter.getItems().addAll("Tous", "en attente", "confirmée", "annulée");
        statusFilter.setValue("Tous");

        FilteredList<Planification> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(planification -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return planification.getAdresse().toLowerCase().contains(lowerCaseFilter) ||
                        planification.getMode().toLowerCase().contains(lowerCaseFilter) ||
                        planification.getStatut().toLowerCase().contains(lowerCaseFilter) ||
                        (planification.getFreelancer() != null &&
                                (planification.getFreelancer().getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                                        planification.getFreelancer().getNom().toLowerCase().contains(lowerCaseFilter))) ||
                        (planification.getUtilisateur() != null &&
                                (planification.getUtilisateur().getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                                        planification.getUtilisateur().getNom().toLowerCase().contains(lowerCaseFilter)));
            });
        });

        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(planification -> {
                if (newValue.equals("Tous")) {
                    return true;
                }
                return planification.getStatut().equals(newValue);
            });
        });

        // Just set the filtered data directly to the ListView
        planificationListView.setItems(filteredData);
    }

    private void loadPlanificationData() {
        try {
            masterData.setAll(servicesPlanification.findAll());
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading data: " + e.getMessage());
        }
    }

    private void updatePlanificationStatus(Planification planification, String newStatus) {
        try {
            planification.setStatut(newStatus);
            servicesPlanification.update(planification);
            loadPlanificationData();
            showAlert("Success", "Status updated successfully!");
        } catch (SQLException e) {
            showAlert("Database Error", "Error updating status: " + e.getMessage());
        }
    }

    private void handleEditPlanification(Planification planification) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit_planification.fxml"));
            Parent root = loader.load();

            EditPlanificationController controller = loader.getController();
            controller.setPlanification(planification);
            controller.setRefreshCallback(this::loadPlanificationData);

            Stage stage = new Stage();
            stage.setTitle("Edit Planification");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Could not load the edit window: " + e.getMessage());
        }
    }

    private void handleDeletePlanification(Planification planification) {
        try {
            if (planification == null) {
                showAlert("Error", "No planification selected");
                return;
            }

            Long id = planification.getId();
            if (id == null) {
                showAlert("Error", "Selected planification has no ID");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Planification #" + id);
            alert.setContentText("This cannot be undone. Proceed?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }

            servicesPlanification.delete(Math.toIntExact(id));
            masterData.removeIf(p -> id.equals(p.getId()));
            showAlert("Success", "Planification #" + id + " deleted");

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to delete:\n" + e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Unexpected error:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleAddPlanification() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_planification.fxml"));
            Parent root = loader.load();

            AddPlanificationController controller = loader.getController();
            controller.setRefreshCallback(this::loadPlanificationData);

            Stage stage = new Stage();
            stage.setTitle("Add New Planification");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Could not load the add window: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}