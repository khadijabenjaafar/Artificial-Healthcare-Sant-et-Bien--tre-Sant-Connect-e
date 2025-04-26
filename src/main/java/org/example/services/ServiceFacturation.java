package org.example.services;
import org.example.entities.Facturation;
import org.example.entities.Ordonnance;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ServiceFacturation implements IService<Facturation> {

    private Connection connection;

    public ServiceFacturation() {
        connection = MyDataBase.getInstance().getMyConnection();
    }

    @Override
    public void ajouter(Facturation facturation) throws SQLException {
        // Vérification si id_ordonnance_id_id existe dans la table ordonnance
        String checkSql = "SELECT COUNT(*) FROM ordonnance WHERE id = ?";
        PreparedStatement checkPst = connection.prepareStatement(checkSql);
        checkPst.setInt(1, facturation.getIdOrdonnanceIdId().getId());
        ResultSet rs = checkPst.executeQuery();
        rs.next();
        int count = rs.getInt(1);

        if (count == 0) {
            System.out.println("L'ID ordonnance n'existe pas dans la table ordonnance.");
            // Arrêter l'insertion si l'ID n'existe pas
            return;
        }

        // Si l'ID ordonnance existe, procéder à l'insertion dans la table facturation
        String sql = "INSERT INTO facturation (id_ordonnance_id_id, date, montant, methode_paiement, statut) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, facturation.getIdOrdonnanceIdId().getId());
        pst.setDate(2, Date.valueOf(facturation.getDateFacturation()));
        pst.setDouble(3, facturation.getMontant());
        pst.setString(4, facturation.getMethodePaiement());
        pst.setString(5, facturation.getStatut());

        pst.executeUpdate();
    }


    @Override
    public void modifier(Facturation facturation) throws SQLException {
        String sql = "UPDATE facturation SET id_ordonnance_id_id = ?, date = ?, montant = ?, methode_paiement = ?, statut = ?  WHERE id = ?";

        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, facturation.getIdOrdonnanceIdId().getId()); // Utilisation de la bonne méthode
        pst.setDate(2, Date.valueOf(facturation.getDateFacturation())); // Convertir LocalDate en java.sql.Date
        pst.setDouble(3, facturation.getMontant());
        pst.setString(4, facturation.getMethodePaiement());
        pst.setString(5, facturation.getStatut());
        pst.setInt(6, facturation.getId());

        pst.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM facturation WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
    }


    @Override
    public List<Facturation> afficher() throws SQLException {
        List<Facturation> facturations = new ArrayList<>();
        String sql = "SELECT * FROM facturation";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            // Récupérer l'ID de l'ordonnance à partir du ResultSet
            int ordonnanceId = rs.getInt("id_ordonnance_id_id");

            // Créer un objet Ordonnance avec l'ID récupéré
            Ordonnance ordonnance = new Ordonnance(ordonnanceId); // Créer l'Ordonnance avec l'ID

            // Créer un objet Facturation en utilisant l'objet Ordonnance
            Facturation facturation = new Facturation(
                    rs.getInt("id"),  // id de la facturation
                    ordonnance,  // L'objet Ordonnance que vous venez de créer
                    rs.getDate("date").toLocalDate(),  // Conversion de la date
                    rs.getDouble("montant"),
                    rs.getString("methode_paiement"),
                    rs.getString("statut")
            );

            facturation.setId(rs.getInt("id"));
            facturations.add(facturation);  // Ajouter à la liste des facturations
        }

        return facturations;
    }
    public List<Facturation> recuperer() throws SQLException {
        List<Facturation> facturations = new ArrayList<>();
        String sql = "SELECT * FROM facturation";

        Connection conn = MyDataBase.getInstance().getMyConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Facturation f = new Facturation();
            f.setId(rs.getInt("id"));
            f.setMontant(rs.getDouble("montant"));
            f.setDateFacturation(rs.getDate("date").toLocalDate());
            f.setMethodePaiement(rs.getString("methode_paiement"));

            facturations.add(f);
        }

        return facturations;
    }


}

