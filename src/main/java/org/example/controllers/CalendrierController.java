package org.example.controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.example.entities.Consultation;
import org.example.entities.RendezVous;
import org.example.services.ServiceConsultation;
import org.example.services.ServiceRendezVous;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class CalendrierController implements Initializable {

    @FXML
    private CalendarView calendarView;

    private ServiceRendezVous serviceRendezVous = new ServiceRendezVous();

    private ServiceConsultation serviceConsultation = new ServiceConsultation();

    public CalendrierController() {
        // Constructeur vide requis par JavaFX
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        calendarView.getStylesheets().add(getClass().getResource("/calendrier.css").toExternalForm());

        if (calendarView != null) {
            calendarView.setVisible(true);
            try {
                afficherEvenements();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void afficherEvenements() throws SQLException {
        // --- Rendez-vous ---
        List<RendezVous> listeRDV = serviceRendezVous.afficher();
        Calendar calendarRDV = new Calendar("Rendez-vous");

        for (RendezVous rdv : listeRDV) {
            Entry<String> entry = new Entry<>("Rendez-Vous : " + rdv.getMotif().toString());
            entry.changeStartDate(rdv.getDateHeure().toLocalDate());
            entry.changeStartTime(rdv.getDateHeure().toLocalTime());
            entry.changeEndDate(rdv.getDateHeure().toLocalDate()); // <-- Correction ici
            entry.changeEndTime(rdv.getDateHeure().toLocalTime().plusMinutes(30));
            calendarRDV.addEntry(entry);
        }

        // --- Consultations ---
        List<Consultation> listeConsult = serviceConsultation.afficher();
        Calendar calendarConsult = new Calendar("Consultations");

        for (Consultation consult : listeConsult) {
            if (consult.getProchainRdv() != null) {
                Entry<String> entry = new Entry<>("Consultation : " + consult.getDiagnostic());
                entry.changeStartDate(consult.getProchainRdv());
                entry.changeStartTime(java.time.LocalTime.of(10, 0));
                entry.changeEndDate(consult.getProchainRdv()); // <-- Correction ici aussi
                entry.changeEndTime(java.time.LocalTime.of(10, 30));
                calendarConsult.addEntry(entry);
            }
        }

        // --- Ajouter les sources au CalendarView ---
        calendarView.getCalendarSources().clear();
        CalendarSource source = new CalendarSource("Agenda");
        source.getCalendars().addAll(calendarRDV, calendarConsult);
        calendarView.getCalendarSources().add(source);

    }


}
