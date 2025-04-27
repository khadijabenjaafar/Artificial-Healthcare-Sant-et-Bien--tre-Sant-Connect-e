package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import org.example.entities.Consultation;
import org.example.services.ServiceConsultation;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatistiqueConsultation {
    @FXML
    private PieChart pieChart;
    private ServiceConsultation serviceConsultation = new ServiceConsultation();

    public void initialize() throws SQLException {
        // Récupérer la liste des consultations
        ObservableList<Consultation> consultations = FXCollections.observableArrayList(serviceConsultation.afficher());

        // Créer une carte pour compter les consultations par 'prochainRdv'
        Map<String, Integer> prochainRdvCount = new HashMap<>();
        for (Consultation consultation : consultations) {
            String prochainRdv = consultation.getProchainRdv().toString();  // Utiliser le champ 'prochainRdv' comme clé
            prochainRdvCount.put(prochainRdv, prochainRdvCount.getOrDefault(prochainRdv, 0) + 1);
        }

        // Préparer les données du graphique circulaire
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : prochainRdvCount.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Définir les données et le titre du graphique circulaire
        pieChart.setData(pieChartData);
        pieChart.setTitle("Répartition des consultations par prochain RDV");
    }
}
