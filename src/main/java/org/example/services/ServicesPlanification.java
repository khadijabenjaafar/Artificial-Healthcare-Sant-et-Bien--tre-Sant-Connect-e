package org.example.services;

import org.example.entities.Planification;
import org.example.entities.Utilisateur;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicesPlanification implements IServices<Planification> {
    private final Connection connection;

    public ServicesPlanification() {
        connection = MyDataBase.getInstance().getMyConnection();
    }

    @Override
    public void add(Planification planification) throws SQLException {
        String sql = "INSERT INTO planification (statut, date, adresse, reponse, mode, freelancer_id, utilisateur_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, planification.getStatut());
            pstmt.setDate(2, Date.valueOf(planification.getDate()));
            pstmt.setString(3, planification.getAdresse());
            pstmt.setString(4, planification.getReponse());
            pstmt.setString(5, planification.getMode());
            pstmt.setLong(6, planification.getFreelancer().getId());
            pstmt.setLong(7, planification.getUtilisateur().getId());
            pstmt.executeUpdate();
        }

    }

    @Override
    public void update(Planification planification) throws SQLException {
        if (planification.getId() == null) {
            throw new IllegalArgumentException("Planification ID must not be null for update.");
        }

        String sql = "UPDATE planification SET statut = ?, date = ?, adresse = ?, reponse = ?, mode = ?, freelancer_id = ?, utilisateur_id = ? " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, planification.getStatut());
            pstmt.setDate(2, Date.valueOf(planification.getDate()));
            pstmt.setString(3, planification.getAdresse());
            pstmt.setString(4, planification.getReponse());
            pstmt.setString(5, planification.getMode());
            pstmt.setLong(6, planification.getFreelancer().getId());
            pstmt.setLong(7, planification.getUtilisateur().getId());
            pstmt.setLong(8, planification.getId()); // ici c’est safe après le check
            pstmt.executeUpdate();
        }
    }


    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM planification WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Deleting planification failed, no rows affected.");
            }
        }
    }

    @Override
    public Planification findById(int id) throws SQLException {
        String sql = "SELECT * FROM planification WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Planification p = new Planification();
                p.setStatut(rs.getString("statut"));
                p.setDate(rs.getDate("date").toLocalDate());
                p.setAdresse(rs.getString("adresse"));
                p.setReponse(rs.getString("reponse"));
                p.setMode(rs.getString("mode"));
                p.setId(rs.getLong("id")); // ➕ à ajouter ici

                // You’ll need to fetch the Utilisateur objects by ID if needed
                // For now, we can just set dummy Utilisateur with only ID set
                var freelancer = new org.example.entities.Utilisateur();
                freelancer.setId(rs.getInt("freelancer_id"));
                p.setFreelancer(freelancer);

                var utilisateur = new org.example.entities.Utilisateur();
                utilisateur.setId(rs.getInt("utilisateur_id"));
                p.setUtilisateur(utilisateur);

                return p;
            }
        }
        return null;
    }

    @Override
    public List<Planification> findAll() throws SQLException {
        List<Planification> planifications = new ArrayList<>();
        String sql = "SELECT * FROM planification";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Planification p = new Planification();
                p.setStatut(rs.getString("statut"));
                p.setDate(rs.getDate("date").toLocalDate());
                p.setAdresse(rs.getString("adresse"));
                p.setReponse(rs.getString("reponse"));
                p.setMode(rs.getString("mode"));
                p.setId(rs.getLong("id"));
                var freelancer = new org.example.entities.Utilisateur();
                freelancer.setId(rs.getInt("freelancer_id"));
                p.setFreelancer(freelancer);

                var utilisateur = new org.example.entities.Utilisateur();
                utilisateur.setId(rs.getInt("utilisateur_id"));
                p.setUtilisateur(utilisateur);

                planifications.add(p);
            }
        }
        return planifications;
    }
    // Add these methods to your existing ServicesPlanification class

    public List<Planification> findByFreelancer(int freelancerId) throws SQLException {
        List<Planification> planifications = new ArrayList<>();
        String sql = "SELECT * FROM planification WHERE freelancer_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, freelancerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                planifications.add(createPlanificationFromResultSet(rs));
            }
        }
        return planifications;
    }

    public List<Planification> findByUser(int userId) throws SQLException {
        List<Planification> planifications = new ArrayList<>();
        String sql = "SELECT * FROM planification WHERE utilisateur_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                planifications.add(createPlanificationFromResultSet(rs));
            }
        }
        return planifications;
    }

    public void updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE planification SET statut = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }

    private Planification createPlanificationFromResultSet(ResultSet rs) throws SQLException {
        Planification p = new Planification();
        p.setId(rs.getLong("id"));
        p.setStatut(rs.getString("statut"));
        p.setDate(rs.getDate("date").toLocalDate());
        p.setAdresse(rs.getString("adresse"));
        p.setReponse(rs.getString("reponse"));
        p.setMode(rs.getString("mode"));

        var freelancer = new Utilisateur();
        freelancer.setId(rs.getInt("freelancer_id"));
        p.setFreelancer(freelancer);

        var utilisateur = new Utilisateur();
        utilisateur.setId(rs.getInt("utilisateur_id"));
        p.setUtilisateur(utilisateur);

        return p;
    }
}
