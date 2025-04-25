package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import org.example.entities.Utilisateur;
import org.example.services.ServiceUtilisateur;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatistiqueUser {
    @FXML
    private PieChart pieChart;
    private ServiceUtilisateur su= new ServiceUtilisateur();

    public void initialize() throws SQLException {
        ObservableList<Utilisateur> users =  FXCollections.observableArrayList(su.afficher());

        Map<String, Integer> roleCount = new HashMap<>();
        for (Utilisateur user : users) {
            roleCount.put(user.getRole().toString(), roleCount.getOrDefault(user.getRole().toString(), 0) + 1);
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : roleCount.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle("Répartition des utilisateurs par rôle");
    }
}
