package com.example.services;



import com.example.entities.Article;
import com.example.entities.Utilisateur;
import com.example.utils.MyDataBase;

import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceArticle implements IService<Article> {
    private Connection connection;


    public ServiceArticle() {
        connection = MyDataBase.getInstance().getMyConnection();
    }


    @Override
    public void ajouter(Article article) throws SQLException {
        String sql = "INSERT INTO article (contenue,titre,datearticle, nbre_vue, id_utilisateur,urlimagearticle) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, article.getContenue());
        pst.setString(2, article.getTitre());
        pst.setDate(3, article.getDateArticle());
        pst.setInt(4, article.getNbreVue());
        pst.setInt(5, article.getUtilisateur().getId());// FK
        pst.setString(6, article.getUrlimagearticle());
        pst.executeUpdate();

    }

    @Override
    public void modifier(Article article) throws SQLException {
        String sql = "UPDATE article SET contenue = ? ,titre = ?, datearticle = ?, nbre_vue = ?, id_utilisateur = ?,urlimagearticle=? WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, article.getContenue());
        pst.setString(2, article.getTitre());
        pst.setDate(3, article.getDateArticle());
        pst.setInt(4, article.getNbreVue());
        pst.setInt(5, article.getUtilisateur().getId());
        pst.setString(6, article.getUrlimagearticle());
        pst.setInt(7, article.getId());

        pst.executeUpdate();

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM article WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();

    }

    @Override
    public List<Article> afficher() throws SQLException {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM article";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            Article a = new Article();
            a.setId(rs.getInt("id"));
            a.setTitre(rs.getString("titre"));
            a.setContenue(rs.getString("contenue"));
            a.setDateArticle(rs.getDate("datearticle"));
            a.setNbreVue(rs.getInt("nbre_vue"));
            a.setUrlimagearticle(rs.getString("urlimagearticle"));


            Utilisateur u = new Utilisateur();
            u.setId(rs.getInt("id_utilisateur")); // Juste l’id ici, tu peux faire un JOIN si tu veux plus
            a.setUtilisateur(u);

            articles.add(a);
        }

        return articles;
    }




    public List<Article> getArticlesByUser(int utilisateurId) {
        List<Article> list = new ArrayList<>();
        String sql = "SELECT * FROM article WHERE id_utilisateur = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, utilisateurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Article a = new Article();
                a.setId(rs.getInt("id"));
                a.setTitre(rs.getString("titre"));
                a.setContenue(rs.getString("contenue"));
                a.setDateArticle(rs.getDate("datearticle"));
                a.setUrlimagearticle(rs.getString("urlimagearticle"));
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("id_utilisateur"));
                a.setUtilisateur(u);


                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }













}
