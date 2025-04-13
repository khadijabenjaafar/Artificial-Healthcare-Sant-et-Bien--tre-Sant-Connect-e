package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.entities.RendezVous;
import org.example.entities.Utilisateur;
import org.example.enums.Mode;
import org.example.enums.Motif;
import org.example.enums.Statut;
import org.example.services.ServiceRendezVous;
import org.example.services.ServiceUtilisateur;

import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ModifierRendezVous {
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> spinnerHeure;
    @FXML private Spinner<Integer> spinnerMinute;
    @FXML private ComboBox<Motif> comboMotif;
    @FXML private ComboBox<Statut> comboStatut;
    @FXML private ComboBox<Mode> comboMode;
    @FXML private TextArea commentaireArea;
    @FXML private ComboBox<Utilisateur> comboMedecin;
    @FXML private ComboBox<Utilisateur> comboPatient;
    @FXML private Button btnModifier;

    @FXML private Label dateErrorLabel;
    @FXML private Label motifErrorLabel;
    @FXML private Label statutErrorLabel;
    @FXML private Label modeErrorLabel;
    @FXML private Label labelErrorCommentaire;
    @FXML private Label medecinErrorLabel;
    @FXML private Label patientErrorLabel;

    private final ServiceRendezVous rendezVousService = new ServiceRendezVous();
    private final ServiceUtilisateur utilisateurService = new ServiceUtilisateur();

    private RendezVous rendezVous; // Rendez-vous à modifier
    private CardRendezVous parentCard;
    public void initialize() {
        spinnerHeure.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9));
        spinnerMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        comboMotif.getItems().addAll(Motif.values());
        comboStatut.getItems().addAll(Statut.values());
        comboMode.getItems().addAll(Mode.values());

        try {
            comboMedecin.getItems().addAll(utilisateurService.getMedecins());
            comboPatient.getItems().addAll(utilisateurService.getPatients());
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des utilisateurs : " + e.getMessage());
        }

        resetErrorLabels();
    }

    // Cette méthode est appelée depuis l'extérieur pour injecter le rendez-vous à modifier
    public void setRendezVous(RendezVous rv) {
        this.rendezVous = rv;

        datePicker.setValue(rv.getDateHeure().toLocalDate());
        spinnerHeure.getValueFactory().setValue(rv.getDateHeure().getHour());
        spinnerMinute.getValueFactory().setValue(rv.getDateHeure().getMinute());

        comboMotif.setValue(rv.getMotif());
        comboStatut.setValue(rv.getStatut());
        comboMode.setValue(rv.getMode());
        commentaireArea.setText(rv.getCommentaire());

        comboMedecin.setValue(rv.getMedecin());
        comboPatient.setValue(rv.getPatient());
    }

    @FXML
    private void modifierRendezVous(javafx.event.ActionEvent event) {
        resetErrorLabels();

        boolean isValid = true;

        LocalDate date = datePicker.getValue();
        Integer heure = spinnerHeure.getValue();
        Integer minute = spinnerMinute.getValue();
        Motif motif = comboMotif.getValue();
        Statut statut = comboStatut.getValue();
        Mode mode = comboMode.getValue();
        String commentaire = commentaireArea.getText();
        Utilisateur medecin = comboMedecin.getValue();
        Utilisateur patient = comboPatient.getValue();

        if (date == null) {
            dateErrorLabel.setText("Date requise");
            dateErrorLabel.setVisible(true);
            isValid = false;
        }

        if (motif == null) {
            motifErrorLabel.setText("Motif requis");
            motifErrorLabel.setVisible(true);
            isValid = false;
        }

        if (statut == null) {
            statutErrorLabel.setText("Statut requis");
            statutErrorLabel.setVisible(true);
            isValid = false;
        }

        if (mode == null) {
            modeErrorLabel.setText("Mode requis");
            modeErrorLabel.setVisible(true);
            isValid = false;
        }

        if (commentaire == null || commentaire.trim().isEmpty()) {
            labelErrorCommentaire.setText("Commentaire requis");
            labelErrorCommentaire.setVisible(true);
            isValid = false;
        }

        if (medecin == null) {
            medecinErrorLabel.setText("Médecin requis");
            medecinErrorLabel.setVisible(true);
            isValid = false;
        }

        if (patient == null) {
            patientErrorLabel.setText("Patient requis");
            patientErrorLabel.setVisible(true);
            isValid = false;
        }

        if (isValid) {
            LocalTime time = LocalTime.of(heure, minute);
            LocalDateTime dateTime = LocalDateTime.of(date, time);

            rendezVous.setDateHeure(dateTime);
            rendezVous.setMotif(motif);
            rendezVous.setStatut(statut);
            rendezVous.setMode(mode);
            rendezVous.setCommentaire(commentaire);
            rendezVous.setMedecin(medecin);
            rendezVous.setPatient(patient);

            try {
                rendezVousService.modifier(rendezVous);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Rendez-vous modifié avec succès !");
                alert.showAndWait();
                // Mettre en surbrillance la carte et la rafraîchir
                if (parentCard != null) {
                    parentCard.refreshData(); // D'abord on recharge les données
                    parentCard.setHighlighted(true); // Ensuite on surligne
                }

// Fermer la fenêtre de modification
                Stage stage = (Stage) btnModifier.getScene().getWindow();
                stage.close();



                // Optionnel : fermer la fenêtre ou afficher une alerte
            } catch (SQLException e) {
                System.err.println("Erreur lors de la modification du rendez-vous : " + e.getMessage());
            }
        }
    }


    private void resetErrorLabels() {
        dateErrorLabel.setVisible(false);
        motifErrorLabel.setVisible(false);
        statutErrorLabel.setVisible(false);
        modeErrorLabel.setVisible(false);
        labelErrorCommentaire.setVisible(false);
        medecinErrorLabel.setVisible(false);
        patientErrorLabel.setVisible(false);
    }
    public void setParentCard(CardRendezVous parentCard) {
        this.parentCard = parentCard;
    }
}
