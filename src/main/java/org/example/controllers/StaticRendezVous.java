package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import org.example.services.ServiceRendezVous;

import java.sql.SQLException;
import java.util.Map;

public class StaticRendezVous {
    @FXML
    private PieChart pieChart;
    private ServiceRendezVous serviceRendezVous = new ServiceRendezVous();

    public void initialize() throws SQLException {
        // Fetch the motif counts
        Map<String, Integer> motifCount = serviceRendezVous.countRendezVousByMotif();

        // Create PieChart data from the motif count
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : motifCount.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Set the pie chart data and title
        pieChart.setData(pieChartData);
        pieChart.setTitle("Répartition des rendez-vous par motif");
    }
}
