package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.entities.Matching;
import org.example.services.ServiceMatching;

import java.sql.SQLException;

public class EditMatchingController {

    @FXML private TextField cinField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dateField;
    @FXML private TextField competencesField;
    @FXML private TextField priceField;

    private Matching matching;
    private Runnable refreshCallback;  // Changed from Consumer<Void> to Runnable
    private ServiceMatching serviceMatching = new ServiceMatching();

    public void setMatching(Matching matching) {
        this.matching = matching;
        // Populate fields with existing data
        cinField.setText(matching.getCin());
        descriptionField.setText(matching.getDescription());
        dateField.setValue(matching.getDate());
        competencesField.setText(matching.getCompetences());
        priceField.setText(String.valueOf(matching.getPrice()));
    }

    public void setRefreshCallback(Runnable callback) {  // Changed parameter type
        this.refreshCallback = callback;
    }

    @FXML

    private void handleSave() {
        if (validateFields()) {
            try {
                // Update the matching object
                matching.setCin(cinField.getText().trim());
                matching.setDescription(descriptionField.getText().trim());
                matching.setDate(dateField.getValue());
                matching.setCompetences(competencesField.getText().trim());
                matching.setPrice(Float.parseFloat(priceField.getText().trim()));

                serviceMatching.update(matching);

                if (refreshCallback != null) {
                    refreshCallback.run();
                }

                closeWindow();
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to update matching: " + e.getMessage());
            } catch (NumberFormatException e) {
                showAlert("Input Error", "Price must be a valid number!");
            }
        }
    }
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (cinField.getText() == null || cinField.getText().trim().isEmpty()) {
            errors.append("• CIN is required.\n");
        } else if (!cinField.getText().matches("\\d{8}")) {  // Tunisian CIN check, 8 digits
            errors.append("• CIN must be exactly 8 digits.\n");
        }

        if (descriptionField.getText() == null || descriptionField.getText().trim().isEmpty()) {
            errors.append("• Description is required.\n");
        }

        if (dateField.getValue() == null) {
            errors.append("• Date is required.\n");
        }

        if (competencesField.getText() == null || competencesField.getText().trim().isEmpty()) {
            errors.append("• Competences field is required.\n");
        }

        if (priceField.getText() == null || priceField.getText().trim().isEmpty()) {
            errors.append("• Price is required.\n");
        } else {
            try {
                float price = Float.parseFloat(priceField.getText().trim());
                if (price < 0) {
                    errors.append("• Price must be a positive number.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Price must be a valid number.\n");
            }
        }

        if (errors.length() > 0) {
            showAlert("Validation Error", "Please fix the following errors:\n\n" + errors.toString());
            return false;
        }

        return true;
    }


    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cinField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}