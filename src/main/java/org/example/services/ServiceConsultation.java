package org.example.services;

import org.example.entities.Consultation;
import org.example.entities.RendezVous;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
}
