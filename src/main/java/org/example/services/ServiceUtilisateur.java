package org.example.services;

import org.example.entities.Utilisateur;
import org.example.utils.MyDataBase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtilisateur {
    private Connection connection;

    public ServiceUtilisateur() {
        connection = MyDataBase.getInstance().getMyConnection();
    }

    public List<Utilisateur> getMedecins() throws SQLException {
        List<Utilisateur> medecins = new ArrayList<>();
        String sql = "SELECT `id`, `nom`, `prenom` FROM `utilisateur` ";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            Utilisateur medecin = new Utilisateur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom")
            );
            medecins.add(medecin);
        }

        return medecins;
    }

    public List<Utilisateur> getPatients() throws SQLException {
        List<Utilisateur> patients = new ArrayList<>();
        String sql = "SELECT `id`, `nom`, `prenom` FROM `utilisateur`";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            Utilisateur patient = new Utilisateur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom")
            );
            patients.add(patient);
        }

        return patients;
    }
}

