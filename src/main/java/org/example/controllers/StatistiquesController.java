package org.example.controllers;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
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
        serviceFacturation = new ServiceFacturation();
    }

    @FXML
    public void initialize() {
        try {
            loadPieChartData();
            loadBarChartData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPieChartData() throws SQLException {
        double totalFactures = serviceFacturation.getTotalFacture();
        double totalImpaye = serviceFacturation.getTotalImpaye();

        PieChart.Data paidData = new PieChart.Data("Payées", totalFactures - totalImpaye);
        PieChart.Data unpaidData = new PieChart.Data("Non Payées", totalImpaye);

        pieChart.getData().clear();
        pieChart.getData().addAll(paidData, unpaidData);

        for (PieChart.Data data : pieChart.getData()) {
            data.getNode().setOnMouseEntered(event -> applyZoomAnimation(data.getNode(), 1.0, 1.1));
            data.getNode().setOnMouseExited(event -> applyZoomAnimation(data.getNode(), 1.1, 1.0));
        }
        Node title = pieChart.lookup(".chart-title");
        if (title != null) {
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        }
    }

    private void loadBarChartData() throws SQLException {
        Map<String, Double> montantsParMois = serviceFacturation.getMontantsParMois();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Montants par Mois");

        for (Map.Entry<String, Double> entry : montantsParMois.entrySet()) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
            series.getData().add(data);

            // Ajouter un écouteur pour l'animation et tooltip après la création du nœud
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    // Animation au survol
                    newNode.setOnMouseEntered(e -> applyZoomAnimation(newNode, 1.0, 1.1));
                    newNode.setOnMouseExited(e -> applyZoomAnimation(newNode, 1.1, 1.0));

                    // Tooltip
                    Tooltip tooltip = new Tooltip(entry.getKey() + " : " + entry.getValue() + " DT");
                    Tooltip.install(newNode, tooltip);
                }
            });
        }
        Node title = barChart.lookup(".chart-title");
        if (title != null) {
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        }
        barChart.setLegendVisible(false);

        barChart.getData().clear();
        barChart.getData().add(series);
    }

    private void applyZoomAnimation(javafx.scene.Node node, double fromScale, double toScale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setFromX(fromScale);
        st.setFromY(fromScale);
        st.setToX(toScale);
        st.setToY(toScale);
        st.play();
    }
}
