package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.entities.RendezVous;
import org.example.enums.Mode;
import org.example.enums.Motif;
import org.example.enums.Statut;
import org.example.services.ServiceRendezVous;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class ModifierRendezVous implements Initializable {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<Motif> motifCombo;

    @FXML
    private ComboBox<Statut> statutCombo;

    @FXML
    private ComboBox<Mode> modeCombo;

    @FXML
    private TextField commentaireField;

    @FXML
    private Button modifierButton;

    @FXML
    private Spinner<Integer> heureSpinner;

    @FXML
    private Spinner<Integer> minuteSpinner;

    private RendezVous rendezVous;
    private CardRendezVous parentController;

    private final ServiceRendezVous service = new ServiceRendezVous();

    public void setRendezVous(RendezVous rv) {
        this.rendezVous = rv;

        // Préremplir les champs
        datePicker.setValue(rv.getDateHeure().toLocalDate());
        heureSpinner.getValueFactory().setValue(rv.getDateHeure().getHour());
        minuteSpinner.getValueFactory().setValue(rv.getDateHeure().getMinute());
        motifCombo.setValue(rv.getMotif());
        statutCombo.setValue(rv.getStatut());
        modeCombo.setValue(rv.getMode());
        commentaireField.setText(rv.getCommentaire());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        motifCombo.getItems().addAll(Motif.values());
        statutCombo.getItems().addAll(Statut.values());
        modeCombo.getItems().addAll(Mode.values());

        heureSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    }

    @FXML
    void handleModifierRendezVous(ActionEvent event) {
        try {
            LocalDateTime dateHeure = LocalDateTime.of(
                    datePicker.getValue(),
                    LocalTime.of(heureSpinner.getValue(), minuteSpinner.getValue())
            );

            rendezVous.setDateHeure(dateHeure);
            rendezVous.setMotif(motifCombo.getValue());
            rendezVous.setStatut(statutCombo.getValue());
            rendezVous.setMode(modeCombo.getValue());
            rendezVous.setCommentaire(commentaireField.getText());

            service.modifier(rendezVous);
            // ✅ Rafraîchir la vue principale si nécessaire
            if (parentController != null) {
                parentController.rafraichirAffichage();
            }


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Rendez-vous modifié avec succès !");
            alert.showAndWait();

            Stage stage = (Stage) modifierButton.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la modification !");
            alert.showAndWait();
        }
    }

    public static void afficherFenetre(RendezVous rendezVous, CardRendezVous parentController) throws IOException {
        FXMLLoader loader = new FXMLLoader(ModifierRendezVous.class.getResource("/ModifierRendezVous.fxml"));
        Parent root = loader.load();

        ModifierRendezVous controller = loader.getController();
        controller.setRendezVous(rendezVous);
        controller.setParentController(parentController);

        Stage stage = new Stage();
        stage.setTitle("Modifier Rendez-vous");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setParentController(CardRendezVous parentController) {
        this.parentController = parentController;
    }


}
