package org.example.controllers;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.example.entities.Planification;
import org.example.services.ServicesPlanification;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatistiquePlanification {

    @FXML
    private PieChart pieChart;

    private final ServicesPlanification servicePlanification = new ServicesPlanification();

    public void initialize() throws SQLException {
        // Récupérer les planifications
        ObservableList<Planification> planifications = FXCollections.observableArrayList(servicePlanification.afficher());

        // Compter les planifications par statut
        Map<String, Integer> statutCount = new HashMap<>();
        for (Planification p : planifications) {
            String statut = p.getStatut();
            statutCount.put(statut, statutCount.getOrDefault(statut, 0) + 1);
        }

        // Palette de couleurs joyeuses
        String[] cheerfulColors = {
                "#006400", "#98FB98", "#2E8B57", "#3CB371",
                "#66CDAA", "#8FBC8F", "#20B2AA", "#98FB98"
        };

        // Créer les données du PieChart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        int colorIndex = 0;
        for (Map.Entry<String, Integer> entry : statutCount.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
            pieChartData.add(data);
        }

        // Configurer le PieChart
        pieChart.setData(pieChartData);
        pieChart.setTitle("Répartition des planifications par statut");

        // Appliquer styles et interactions après que les nœuds sont prêts
        Platform.runLater(() -> {
            int i = 0;
            for (PieChart.Data data : pieChartData) {
                Node node = data.getNode();
                if (node != null) {
                    String color = cheerfulColors[i % cheerfulColors.length];
                    node.setStyle("-fx-pie-color: " + color + ";");

                    // Tooltip
                    Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue() + " planifications");
                    Tooltip.install(node, tooltip);

                    // Zoom au survol
                    node.setOnMouseEntered(e -> applyZoomAnimation(node, 1.0, 1.1));
                    node.setOnMouseExited(e -> applyZoomAnimation(node, 1.1, 1.0));
                }
                i++;
            }
            Node title = pieChart.lookup(".chart-title");
            if (title != null) {
                title.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            }
            // Appliquer les couleurs aux pastilles de légende
            Node legend = pieChart.lookup(".chart-legend");
            if (legend != null) {
                int j = 0;
                for (Node symbol : legend.lookupAll(".chart-legend-item-symbol")) {
                    String color = cheerfulColors[j % cheerfulColors.length];
                    symbol.setStyle("-fx-background-color: " + color + ";");
                    j++;
                }
            }

            // Supprimer les étiquettes internes si souhaité :
            // for (Node label : pieChart.lookupAll(".chart-pie-label")) {
            //     label.setVisible(false);
            // }
        });
    }

    private void applyZoomAnimation(Node node, double fromScale, double toScale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setFromX(fromScale);
        st.setFromY(fromScale);
        st.setToX(toScale);
        st.setToY(toScale);
        st.play();
    }
}
