package org.example.services;


import org.example.entities.Utilisateur;
import org.example.services.IService;
import org.example.utils.MyDataBase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtilisateur implements IService<Utilisateur> {



    private Connection connection;

    public ServiceUtilisateur() {
        connection = MyDataBase.getInstance().getMyConnection();
    }
    @Override
    public void ajouter(Utilisateur utilisateur) throws SQLException {

    }

    @Override
    public void modifier(Utilisateur utilisateur) throws SQLException {

    }

    @Override
    public void supprimer(int id) throws SQLException {

    }



    @Override
    public List<Utilisateur> afficher() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT id, nom FROM utilisateur"; // adapte selon ta table
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            Utilisateur u = new Utilisateur();
            u.setId(rs.getInt("id"));
            u.setNom(rs.getString("nom"));
            utilisateurs.add(u);
        }
        return utilisateurs;
    }
}


