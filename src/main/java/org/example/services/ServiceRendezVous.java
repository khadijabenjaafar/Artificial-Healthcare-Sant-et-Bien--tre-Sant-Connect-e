package org.example.services;

import org.example.entities.RendezVous;
import org.example.entities.Utilisateur;
import org.example.enums.Mode;
import org.example.enums.Motif;
import org.example.enums.Statut;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ServiceRendezVous implements IService<RendezVous> {
    private Connection connection;
    public ServiceRendezVous(){
        connection= MyDataBase.getInstance().getMyConnection();
    }
    @Override
    public void ajouter(RendezVous rendezVous) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = rendezVous.getDateHeure().format(formatter);

        String sql = "INSERT INTO `rendez_vous` (`id_patient_id`, `id_medecin_id`, `date_heure`, `motif`, `statut`, `mode`, `commantaire`) VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, rendezVous.getPatient().getId()); // Clé étrangère patient
        ps.setInt(2, rendezVous.getMedecin().getId()); // Clé étrangère médecin
        ps.setString(3, rendezVous.getDateHeure().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        ps.setString(4, rendezVous.getMotif().name());
        ps.setString(5, rendezVous.getStatut().name());
        ps.setString(6, rendezVous.getMode().name());
        ps.setString(7, rendezVous.getCommentaire());

        ps.executeUpdate();
    }

    @Override
    public void modifier(RendezVous rendezVous) throws SQLException {
        String sql = "UPDATE `rendez_vous` SET `id_patient_id`=?, `id_medecin_id`=?, `date_heure`=?, `motif`=?, `statut`=?, `mode`=?, `commantaire`=? WHERE `id`=?";

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, rendezVous.getPatient().getId()); // Clé étrangère patient
        ps.setInt(2, rendezVous.getMedecin().getId()); // Clé étrangère médecin
        ps.setString(3, rendezVous.getDateHeure().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        ps.setString(4, rendezVous.getMotif().name());
        ps.setString(5, rendezVous.getStatut().name());
        ps.setString(6, rendezVous.getMode().name());
        ps.setString(7, rendezVous.getCommentaire());
        ps.setInt(8, rendezVous.getId()); // ID du rendez-vous à modifier

        ps.executeUpdate();

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql= "DELETE FROM `rendez_vous` WHERE id=?";
        PreparedStatement ps=connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();

    }

    @Override
    public List<RendezVous> afficher() throws SQLException {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String sql = "SELECT rdv.id, rdv.date_heure, rdv.motif, rdv.statut, rdv.mode, rdv.commantaire, " +
                "patient.id AS patient_id, patient.nom AS patient_nom, patient.prenom AS patient_prenom, " +
                "medecin.id AS medecin_id, medecin.nom AS medecin_nom, medecin.prenom AS medecin_prenom " +
                "FROM rendez_vous rdv " +
                "JOIN utilisateur patient ON rdv.id_patient_id = patient.id " +
                "JOIN utilisateur medecin ON rdv.id_medecin_id = medecin.id";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            RendezVous rdv = new RendezVous();
            rdv.setId(rs.getInt("id"));
            rdv.setDateHeure(rs.getTimestamp("date_heure").toLocalDateTime());
            // Récupérer les valeurs de la base de données
            String motifString = rs.getString("motif").toLowerCase();
            String statutString = rs.getString("statut").toLowerCase();
            String modeString = rs.getString("mode").toLowerCase();

            // Convertir les chaînes de la base de données en énumérations Java
            try {
                rdv.setMotif(Motif.valueOf(motifString));
                rdv.setStatut(Statut.valueOf(statutString));
                // Remplacer les espaces par des underscores pour correspondre aux valeurs des énumérations
                rdv.setMode(Mode.valueOf(modeString.replace(" ", "_").toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.out.println("Erreur de conversion de l'enum : " + e.getMessage());
            }

            rdv.setCommentaire(rs.getString("commantaire"));

            Utilisateur patient = new Utilisateur(
                    rs.getInt("patient_id"),
                    rs.getString("patient_nom"),
                    rs.getString("patient_prenom")
            );
            Utilisateur medecin = new Utilisateur(
                    rs.getInt("medecin_id"),
                    rs.getString("medecin_nom"),
                    rs.getString("medecin_prenom")
            );
            rdv.setPatient(patient);
            rdv.setMedecin(medecin);


            rendezVousList.add(rdv);
        }

        return rendezVousList;

    }
}
