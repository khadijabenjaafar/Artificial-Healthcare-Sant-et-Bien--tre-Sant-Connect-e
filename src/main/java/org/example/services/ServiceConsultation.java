package org.example.services;

import org.example.entities.Consultation;
import org.example.entities.RendezVous;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceConsultation implements IService<Consultation> {
    private Connection connection;
    public ServiceConsultation() {connection= MyDataBase.getInstance().getMyConnection();}

    @Override
    public void ajouter(Consultation consultation) throws SQLException {
        String sql = "INSERT INTO consultation (id_rendez_vous, diagnostic, traitement, observation, prix, prochain_rdv, duree) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, consultation.getRendezVous().getId()); // ID du rendez-vous
        ps.setString(2, consultation.getDiagnostic());
        ps.setString(3, consultation.getTraitement());
        ps.setString(4, consultation.getObservation());
        ps.setString(5, consultation.getPrix());
        ps.setDate(6, java.sql.Date.valueOf(consultation.getProchainRdv())); // Conversion LocalDate -> SQL Date
        ps.setString(7, consultation.getDuree());

        ps.executeUpdate();
    }

    @Override
    public void modifier(Consultation consultation) throws SQLException {
        String sql = "UPDATE consultation SET id_rendez_vous = ?, diagnostic = ?, traitement = ?, observation = ?, prix = ?, prochain_rdv = ?, duree = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, consultation.getRendezVous().getId()); // ID du rendez-vous
        ps.setString(2, consultation.getDiagnostic());
        ps.setString(3, consultation.getTraitement());
        ps.setString(4, consultation.getObservation());
        ps.setString(5, consultation.getPrix()); // Prix en DECIMAL
        ps.setDate(6, java.sql.Date.valueOf(consultation.getProchainRdv())); // Conversion LocalDate -> SQL Date
        ps.setString(7, consultation.getDuree()); // Durée en DECIMAL
        ps.setInt(8, consultation.getId()); // ID de la consultation
        ps.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql= "DELETE FROM `consultation` WHERE id=?";
        PreparedStatement ps=connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();

    }

    @Override
    public List<Consultation> afficher() throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT c.id, c.diagnostic, c.traitement, c.observation, c.prix, c.prochain_rdv, c.duree, " +
                "rdv.id AS rdv_id, rdv.date_heure " +
                "FROM consultation c " +
                "JOIN rendez_vous rdv ON c.id_rendez_vous = rdv.id";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            Consultation consultation = new Consultation();
            consultation.setId(rs.getInt("id"));
            consultation.setDiagnostic(rs.getString("diagnostic"));
            consultation.setTraitement(rs.getString("traitement"));
            consultation.setObservation(rs.getString("observation"));
            consultation.setPrix(rs.getString("prix"));
            consultation.setProchainRdv(rs.getDate("prochain_rdv").toLocalDate());
            consultation.setDuree(rs.getString("duree"));

            // Associer le rendez-vous
            RendezVous rdv = new RendezVous();
            rdv.setId(rs.getInt("rdv_id"));
            rdv.setDateHeure(rs.getTimestamp("date_heure").toLocalDateTime());
            consultation.setRendezVous(rdv);

            consultations.add(consultation);
        }

        return consultations;
    }

    public Map<String, Integer> countConsultationsByMonth() throws SQLException {
        Map<String, Integer> monthCount = new HashMap<>();
        String sql = "SELECT EXTRACT(MONTH FROM prochain_rdv) AS month, EXTRACT(YEAR FROM prochain_rdv) AS year, COUNT(*) AS count " +
                "FROM consultation " +
                "GROUP BY EXTRACT(YEAR FROM prochain_rdv), EXTRACT(MONTH FROM prochain_rdv) " +
                "ORDER BY year, month";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            int month = rs.getInt("month");
            int year = rs.getInt("year");
            int count = rs.getInt("count");
            String monthName = getMonthName(month); // Convertir le mois en texte
            String yearMonth = monthName + " " + year; // Format du mois et année
            monthCount.put(yearMonth, count);
        }

        return monthCount;
    }

    private String getMonthName(int month) {
        switch (month) {
            case 1: return "Janvier";
            case 2: return "Février";
            case 3: return "Mars";
            case 4: return "Avril";
            case 5: return "Mai";
            case 6: return "Juin";
            case 7: return "Juillet";
            case 8: return "Août";
            case 9: return "Septembre";
            case 10: return "Octobre";
            case 11: return "Novembre";
            case 12: return "Décembre";
            default: return "";
        }
    }

}
