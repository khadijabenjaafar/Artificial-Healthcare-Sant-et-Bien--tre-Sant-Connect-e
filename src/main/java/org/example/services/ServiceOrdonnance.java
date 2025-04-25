package org.example.services;

import org.example.entities.Ordonnance;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceOrdonnance implements IService<Ordonnance> {

    private Connection connection;

    public ServiceOrdonnance() {
        connection = MyDataBase.getInstance().getMyConnection();
    }

    public void ajouter(Ordonnance ordonnance) throws SQLException {
        String sql = "INSERT INTO ordonnance (date, medicaments, commantaire, duree_utilisation, quantite_utilisation) " +
                "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, ordonnance.getDate().toString()); // convert LocalDate to String
        pst.setString(2, ordonnance.getMedicaments());
        pst.setString(3, ordonnance.getCommantaire());
        pst.setString(4, ordonnance.getDureeUtilisation());
        pst.setString(5, ordonnance.getQuantiteUtilisation());

        pst.executeUpdate();
    }

    public void modifier(Ordonnance ordonnance) throws SQLException {

        String sql = "UPDATE ordonnance SET date = ?, medicaments = ?, commantaire = ?, duree_utilisation = ?, quantite_utilisation = ? WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);

        // Vérifie que la date n'est pas nulle
        if (ordonnance.getDate() != null) {
            pst.setString(1, ordonnance.getDate().toString());
        } else {
            pst.setNull(1, Types.DATE); // ou Types.VARCHAR si tu stockes la date comme string
        }

        pst.setString(2, ordonnance.getMedicaments());
        pst.setString(3, ordonnance.getCommantaire());
        pst.setString(4, ordonnance.getDureeUtilisation());
        pst.setString(5, ordonnance.getQuantiteUtilisation());

        // Utilisation de setLong pour les types Long
        pst.setLong(6, ordonnance.getId()); // ⚠️ Assure-toi que getId() retourne Long

        int rowsUpdated = pst.executeUpdate();
        if (rowsUpdated == 0) {
            System.out.println("Aucune ordonnance modifiée. Vérifie l'ID.");
        } else {
            System.out.println("Ordonnance mise à jour avec succès !");
        }
    }

    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM ordonnance WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);

        int rowsAffected = pst.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Ordonnance supprimée avec succès.");
        } else {
            System.out.println("Aucune ordonnance trouvée avec l'ID : " + id);
        }
    }

    @Override
    public List<Ordonnance> afficher() throws SQLException {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM `ordonnance`"; // Pas besoin d'id_consultation_id ici
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            Ordonnance o = new Ordonnance();
            o.setId(rs.getInt("id"));
            o.setMedicaments(rs.getString("medicaments"));
            o.setCommantaire(rs.getString("commantaire"));
            o.setDureeUtilisation(rs.getString("duree_utilisation"));
            o.setQuantiteUtilisation(rs.getString("quantite_utilisation"));

            ordonnances.add(o);
        }
        return ordonnances;
    }

    public List<Ordonnance> recuperer() throws SQLException {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnance";

        Connection conn = MyDataBase.getInstance().getMyConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Ordonnance o = new Ordonnance();
            o.setId(rs.getInt("id"));
            o.setDate(rs.getDate("date").toLocalDate());
            o.setMedicaments(rs.getString("medicaments"));
            o.setCommantaire(rs.getString("commantaire"));
            o.setDureeUtilisation(rs.getString("duree_utilisation"));
            o.setQuantiteUtilisation(rs.getString("quantite_utilisation"));

            ordonnances.add(o);
        }

        return ordonnances;
    }
}
