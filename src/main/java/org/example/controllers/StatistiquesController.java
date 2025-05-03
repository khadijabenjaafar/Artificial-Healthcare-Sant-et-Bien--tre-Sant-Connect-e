package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import org.example.services.ServiceFacturation;

import java.sql.SQLException;
import java.util.Map;

public class StatistiquesController {

    @FXML
    private PieChart pieChart;
    @FXML
    private BarChart<String, Number> barChart;

    private ServiceFacturation serviceFacturation;

    public StatistiquesController() {
        serviceFacturation = new ServiceFacturation(); // Instancier le service
    }

    @FXML
    public void initialize() {
        try {
            // Charger les statistiques pour le PieChart (factures payées vs non payées)
            loadPieChartData();

            // Charger les statistiques pour le BarChart (montants par mois)
            loadBarChartData();
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer les exceptions de SQL
        }
    }

    private void loadPieChartData() throws SQLException {
        // Récupérer les totaux des factures payées et non payées
        double totalFactures = serviceFacturation.getTotalFacture();
        double totalImpaye = serviceFacturation.getTotalImpaye();

        // Créer les données pour le PieChart
        PieChart.Data paidData = new PieChart.Data("Payées", totalFactures - totalImpaye);
        PieChart.Data unpaidData = new PieChart.Data("Non Payées", totalImpaye);

        // Ajouter les données au PieChart
        pieChart.getData().clear();
        pieChart.getData().addAll(paidData, unpaidData);
    }

    private void loadBarChartData() throws SQLException {
        // Récupérer les montants par mois
        Map<String, Double> montantsParMois = serviceFacturation.getMontantsParMois();

        // Créer une série de données pour le BarChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Montants par Mois");

        for (Map.Entry<String, Double> entry : montantsParMois.entrySet()) {
            String mois = entry.getKey(); // Ex : "2025-01"
            Double montant = entry.getValue(); // Montant total pour ce mois
            series.getData().add(new XYChart.Data<>(mois, montant)); // Ajouter à la série
        }

        // Ajouter la série au BarChart
        barChart.getData().clear();
        barChart.getData().add(series);
    }
}
