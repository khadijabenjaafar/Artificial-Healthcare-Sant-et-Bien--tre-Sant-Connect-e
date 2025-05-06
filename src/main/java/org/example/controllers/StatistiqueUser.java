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
import org.example.entities.Utilisateur;
import org.example.services.ServiceUtilisateur;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatistiqueUser {
    @FXML
    private PieChart pieChart;

    private final ServiceUtilisateur su = new ServiceUtilisateur();

    public void initialize() throws SQLException {
        ObservableList<Utilisateur> users = FXCollections.observableArrayList(su.afficher());

        // Compter les rôles
        Map<String, Integer> roleCount = new HashMap<>();
        for (Utilisateur user : users) {
            roleCount.merge(user.getRole().toString(), 1, Integer::sum);
        }

        // Couleurs vertes distinctes
        String[] greenShades = {
                "#006400", "#2E8B57", "#66CDAA", "#8FBC8F",
                "#98FB98", "#8FBC8F", "#20B2AA", "#98FB98"
        };

        // Créer les données du graphique
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        int colorIndex = 0;
        for (Map.Entry<String, Integer> entry : roleCount.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            colorIndex++;
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle("Répartition des utilisateurs par rôle");

        Platform.runLater(() -> {
            int i = 0;
            for (PieChart.Data data : pieChartData) {
                Node node = data.getNode();
                String color = greenShades[i % greenShades.length];
                node.setStyle("-fx-pie-color: " + color + ";");

                // Tooltip
                int count = (int) data.getPieValue();
                Tooltip tooltip = new Tooltip(data.getName() + " : " + count + " utilisateur" + (count > 1 ? "s" : ""));
                Tooltip.install(node, tooltip);

                // Animation d'apparition
                ScaleTransition appear = new ScaleTransition(Duration.seconds(0.5), node);
                appear.setFromX(0);
                appear.setFromY(0);
                appear.setToX(1);
                appear.setToY(1);
                appear.play();

                // Survol
                node.setOnMouseEntered(e -> {
                    ScaleTransition st = new ScaleTransition(Duration.seconds(0.2), node);
                    st.setToX(1.1);
                    st.setToY(1.1);
                    st.play();
                });
                node.setOnMouseExited(e -> {
                    ScaleTransition st = new ScaleTransition(Duration.seconds(0.2), node);
                    st.setToX(1);
                    st.setToY(1);
                    st.play();
                });

                // Zoom au clic
                node.setOnMouseClicked(e -> {
                    for (PieChart.Data otherData : pieChartData) {
                        Node otherNode = otherData.getNode();
                        if (otherNode != null) {
                            ScaleTransition reset = new ScaleTransition(Duration.seconds(0.2), otherNode);
                            reset.setToX(1);
                            reset.setToY(1);
                            reset.play();
                        }
                    }
                    ScaleTransition zoom = new ScaleTransition(Duration.seconds(0.3), node);
                    zoom.setToX(1.3);
                    zoom.setToY(1.3);
                    zoom.play();
                });

                i++;
            }

            // ✅ Titre en gras
            Node title = pieChart.lookup(".chart-title");
            if (title != null) {
                title.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            }

            // ✅ Appliquer les couleurs à la légende
            Node legend = pieChart.lookup(".chart-legend");
            if (legend != null) {
                int j = 0;
                for (Node legendItem : legend.lookupAll(".chart-legend-item-symbol")) {
                    legendItem.setStyle("-fx-background-color: " + greenShades[j % greenShades.length] + ";");
                    j++;
                }
            }

            // ✅ Supprimer les étiquettes autour du camembert
            for (Node label : pieChart.lookupAll(".chart-pie-label")) {
                label.setVisible(false);
            }
        });
    }
}
