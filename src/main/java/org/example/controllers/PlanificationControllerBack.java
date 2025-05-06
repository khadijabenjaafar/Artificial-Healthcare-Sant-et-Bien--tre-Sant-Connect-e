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
import org.example.entities.Planification;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import org.example.services.ServicesPlanification;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class PlanificationControllerBack implements Initializable {

    @FXML private ListView<Planification> planificationListView;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    public Utilisateur CurrentUser= UserConnecter.getInstance().getUserConnecter();

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
        Utilisateur CurrentUser = UserConnecter.getInstance().getUserConnecter(); // Inject current user

        planificationListView.setCellFactory(param -> new ListCell<Planification>() {
            @Override
            protected void updateItem(Planification planification, boolean empty) {
                super.updateItem(planification, empty);

                if (empty || planification == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox(8);
                    container.setPadding(new Insets(12));
                    container.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

                    // Top row
                    HBox topRow = new HBox(15);
                    Label idLabel = new Label("#" + planification.getId());
                    idLabel.setStyle("-fx-font-weight: bold;");

                    Label dateLabel = new Label(planification.getDate().toString());
                    dateLabel.setStyle("-fx-text-fill: #555555;");

                    Label statusLabel = new Label(planification.getStatut());
                    statusLabel.setTextFill(getStatusColor(planification.getStatut()));
                    statusLabel.setStyle("-fx-font-weight: bold;");

                    topRow.getChildren().addAll(idLabel, dateLabel, statusLabel);

                    // Details
                    VBox detailsBox = new VBox(5);
                    Label modeLabel = new Label("Mode: " + planification.getMode());
                    Label addressLabel = new Label("Address: " + planification.getAdresse());

                    // People
                    HBox peopleBox = new HBox(15);
                    String freelancerInfo = "Freelancer: ";
                    if (planification.getFreelancer() != null) {
                        freelancerInfo += planification.getFreelancer().getId() + " - " +
                                planification.getFreelancer().getPrenom() + " " +
                                planification.getFreelancer().getNom();
                        System.out.println("Displaying freelancer: " + planification.getFreelancer()); // Debug
                    } else {
                        freelancerInfo += "N/A";
                        System.out.println("No freelancer for planification: " + planification.getId()); // Debug
                    }
                    Label freelancerLabel = new Label(freelancerInfo);
                    Label userLabel = new Label("User: " + CurrentUser.getPrenom() + " " + CurrentUser.getNom());

                    peopleBox.getChildren().addAll(freelancerLabel, userLabel);

                    detailsBox.getChildren().addAll(modeLabel, addressLabel, peopleBox);

                    // Annulée reason
                    if ("annulée".equals(planification.getStatut())) {
                        Label reasonLabel = new Label("Reason: " +
                                (planification.getReponse() != null ? planification.getReponse() : "No reason provided"));
                        reasonLabel.setTextFill(Color.RED);
                        detailsBox.getChildren().add(reasonLabel);
                    }

                    // Action buttons
                    HBox actionBox = new HBox(10);
                    Button editBtn = new Button("Edit");
                    Button deleteBtn = new Button("Delete");
                    Button confirmBtn = new Button("Confirm");
                    Button cancelBtn = new Button("Cancel");

                    editBtn.getStyleClass().add("edit-button");
                    deleteBtn.getStyleClass().add("delete-button");
                    confirmBtn.getStyleClass().add("confirm-button");
                    cancelBtn.getStyleClass().add("cancel-button");

                    // Add logic to restrict actions to owner or based on role
                    boolean isOwner = (planification.getUtilisateur() != null &&
                            planification.getUtilisateur().getId() == CurrentUser.getId()) ||
                            (planification.getFreelancer() != null &&
                                    planification.getFreelancer().getId() == CurrentUser.getId());

                    if (isOwner) {
                        editBtn.setOnAction(e -> handleEditPlanification(planification));
                        deleteBtn.setOnAction(e -> handleDeletePlanification(planification));
                        confirmBtn.setOnAction(e -> updatePlanificationStatus(planification, "confirmée"));
                        cancelBtn.setOnAction(e -> handleCancelPlanification(planification));

                        if ("en attente".equals(planification.getStatut())) {
                            actionBox.getChildren().addAll(editBtn, deleteBtn, confirmBtn, cancelBtn);
                        } else {
                            actionBox.getChildren().addAll(editBtn, deleteBtn);
                        }
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

                // Secure string comparison with null checks
                boolean matchAdresse = planification.getAdresse() != null && planification.getAdresse().toLowerCase().contains(lowerCaseFilter);
                boolean matchMode = planification.getMode() != null && planification.getMode().toLowerCase().contains(lowerCaseFilter);
                boolean matchStatut = planification.getStatut() != null && planification.getStatut().toLowerCase().contains(lowerCaseFilter);

                boolean matchFreelancer = false;
                if (planification.getFreelancer() != null) {
                    String prenom = planification.getFreelancer().getPrenom();
                    String nom = planification.getFreelancer().getNom();
                    matchFreelancer = (prenom != null && prenom.toLowerCase().contains(lowerCaseFilter)) ||
                            (nom != null && nom.toLowerCase().contains(lowerCaseFilter));
                }

                boolean matchUtilisateur = false;
                if (planification.getUtilisateur() != null) {
                    String prenom = planification.getUtilisateur().getPrenom();
                    String nom = planification.getUtilisateur().getNom();
                    matchUtilisateur = (prenom != null && prenom.toLowerCase().contains(lowerCaseFilter)) ||
                            (nom != null && nom.toLowerCase().contains(lowerCaseFilter));
                }

                return matchAdresse || matchMode || matchStatut || matchFreelancer || matchUtilisateur;
            });
        });

        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(planification -> {
                if (newValue.equals("Tous")) {
                    return true;
                }
                return newValue.equals(planification.getStatut());
            });
        });

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
            System.out.println("Updating status from: " + planification.getStatut() + " to: " + newStatus);
            planification.setStatut(newStatus);
            servicesPlanification.update(planification);
            System.out.println("Update successful, new status: " + planification.getStatut());

            // Force refresh of the ListView
            planificationListView.refresh();

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
    private void handleCancelPlanification(Planification planification) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancel Planification");
        dialog.setHeaderText("Enter cancellation reason for #" + planification.getId());
        dialog.setContentText("Reason:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().isEmpty()) {
            try {
                planification.setStatut("annulée");
                planification.setReponse(result.get());
                servicesPlanification.update(planification);

                // Refresh the view
                int index = masterData.indexOf(planification);
                if (index >= 0) {
                    masterData.set(index, planification);
                }

                showAlert("Success", "Planification cancelled successfully with reason: " + result.get());
            } catch (SQLException e) {
                showAlert("Error", "Failed to update planification: " + e.getMessage());
            }
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}