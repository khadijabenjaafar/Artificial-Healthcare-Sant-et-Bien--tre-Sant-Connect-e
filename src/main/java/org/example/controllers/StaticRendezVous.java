package org.example.controllers;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import org.example.services.ServiceRendezVous;

import java.sql.SQLException;
import java.util.*;

public class StaticRendezVous {
    @FXML
    private BarChart<String, Number> barChart;

    private final ServiceRendezVous serviceRendezVous = new ServiceRendezVous();

    private final List<String> barColors = Arrays.asList(
            "#FF6F61", "#6B5B95", "#88B04B", "#FFA500", "#009B77", "#D65076", "#45B8AC", "#EFC050"
    );

    private final List<Node> allBarNodes = new ArrayList<>();

    public void initialize() throws SQLException {
        Map<String, Integer> motifCount = serviceRendezVous.countRendezVousByMotif();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        int colorIndex = 0;

        for (Map.Entry<String, Integer> entry : motifCount.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(data);

            String color = barColors.get(colorIndex % barColors.size());

            data.nodeProperty().addListener((obs, oldNode, node) -> {
                if (node != null) {
                    node.setStyle("-fx-bar-fill: " + color + ";");
                    node.setScaleY(0); // Commence invisible
                    allBarNodes.add(node);
                }
            });

            colorIndex++;
        }

        barChart.getData().add(series);

        // Définir et styliser le titre du graphique
        barChart.setTitle("Répartition des rendez-vous par motif");

        // Appliquer un style au titre (directement sur le titre du BarChart)
        barChart.lookup(".chart-title").setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-font-family: Arial, sans-serif;"
        );

        // Définir les titres des axes
        barChart.getXAxis().setLabel("Motif");
        barChart.getYAxis().setLabel("Nombre de rendez-vous");

        // Styliser les titres des axes (axis label)
        barChart.getXAxis().lookup(".axis-label").setStyle(
                "-fx-text-fill: #2c3e50;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;"
        );
        barChart.getYAxis().lookup(".axis-label").setStyle(
                "-fx-text-fill: #2c3e50;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;"
        );

        // Styliser les ticks (graduations numériques et textuelles)
        barChart.getXAxis().lookupAll(".tick-label").forEach(tick ->
                tick.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;")
        );
        barChart.getYAxis().lookupAll(".tick-label").forEach(tick ->
                tick.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;")
        );

        // Configurer l'axe Y pour afficher des nombres entiers sans décimales
        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
        yAxis.setAutoRanging(false);  // Empêche l'auto échelle
        yAxis.setLowerBound(0);  // Limite inférieure à 0
        yAxis.setUpperBound(motifCount.size() * 2);  // Ajuste la limite supérieure
        yAxis.setTickUnit(1);  // Unité de tick (affiche seulement des entiers)

        // Formater les ticks pour ne pas avoir de décimales
        yAxis.setTickLabelFormatter(new javafx.scene.chart.NumberAxis.DefaultFormatter(yAxis, "0", null));

        // Styliser le fond du graphique
        Node background = barChart.lookup(".chart-plot-background");
        if (background != null) {
            background.setStyle("-fx-background-color: #f9f9f9;");
        }

        // Désactiver les lignes verticales si tu veux
        barChart.setVerticalGridLinesVisible(false);
        barChart.setLegendVisible(false);

        // Lancer l'animation au survol
        barChart.setOnMouseEntered(event -> animateAllBars());
    }

    private void animateAllBars() {
        for (Node node : allBarNodes) {
            ScaleTransition st = new ScaleTransition(Duration.millis(600), node);
            st.setFromY(0);
            st.setToY(1);
            st.play();
        }
    }
}
