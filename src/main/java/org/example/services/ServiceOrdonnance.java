package org.example.services;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
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
        pst.setString(1, ordonnance.getDate().toString());
        pst.setString(2, ordonnance.getMedicaments());
        pst.setString(3, ordonnance.getCommantaire());
        pst.setString(4, ordonnance.getDureeUtilisation());
        pst.setString(5, ordonnance.getQuantiteUtilisation());

        pst.executeUpdate();
    }

    public void modifier(Ordonnance ordonnance) throws SQLException {
        String sql = "UPDATE ordonnance SET date = ?, medicaments = ?, commantaire = ?, duree_utilisation = ?, quantite_utilisation = ? WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);

        if (ordonnance.getDate() != null) {
            pst.setString(1, ordonnance.getDate().toString());
        } else {
            pst.setNull(1, Types.DATE);
        }

        pst.setString(2, ordonnance.getMedicaments());
        pst.setString(3, ordonnance.getCommantaire());
        pst.setString(4, ordonnance.getDureeUtilisation());
        pst.setString(5, ordonnance.getQuantiteUtilisation());
        pst.setLong(6, ordonnance.getId());

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
        String sql = "SELECT * FROM `ordonnance`";
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

        Statement stmt = connection.createStatement();
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

    public List<Ordonnance> searchOrdonnances(String keyword) throws SQLException {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnance WHERE " +
                "medicaments LIKE ? OR " +
                "commantaire LIKE ? OR " +
                "duree_utilisation LIKE ? OR " +
                "quantite_utilisation LIKE ? OR " +
                "date LIKE ?";

        PreparedStatement pst = connection.prepareStatement(sql);
        String searchPattern = "%" + keyword + "%";

        pst.setString(1, searchPattern);
        pst.setString(2, searchPattern);
        pst.setString(3, searchPattern);
        pst.setString(4, searchPattern);
        pst.setString(5, searchPattern);

        ResultSet rs = pst.executeQuery();

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

    public Map<String, Integer> getNombreOrdonnancesParMois() {
        Map<String, Integer> statistiques = new HashMap<>();

        String sql = "SELECT MONTH(date) as mois, COUNT(*) as nombre FROM ordonnance GROUP BY mois";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int mois = rs.getInt("mois");
                int nombre = rs.getInt("nombre");

                String moisNom = LocalDate.of(2023, mois, 1)
                        .getMonth()
                        .getDisplayName(TextStyle.FULL, Locale.FRENCH);

                statistiques.put(moisNom, nombre);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return statistiques;
    }
}