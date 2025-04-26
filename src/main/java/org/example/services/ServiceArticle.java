
package org.example.services;


import org.example.entities.Article;
import org.example.entities.Utilisateur;
import org.example.services.IService;
import org.example.utils.MyDataBase;

import java.sql.*;
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



    public void ajouterOuMettreAJourRating(int idUtilisateur, int idArticle, int note) throws SQLException {
       // Connection conn = MyConnection.getInstance().getCnx();

        String checkSql = "SELECT rating FROM article_rating WHERE user_id = ? AND article_id = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkSql);
        checkStmt.setInt(1, idUtilisateur);
        checkStmt.setInt(2, idArticle);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            int ancienneNote = rs.getInt("rating");
            if (ancienneNote != note) {
                // Mise à jour uniquement si la note est différente
                String updateSql = "UPDATE article_rating SET rating = ?, date_rating = NOW() WHERE user_id = ? AND article_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setInt(1, note);
                updateStmt.setInt(2, idUtilisateur);
                updateStmt.setInt(3, idArticle);
                updateStmt.executeUpdate();
                System.out.println("🔁 Rating mis à jour !");
            } else {
                System.out.println("✅ Même rating, aucune modification.");
            }
        } else {
            // Pas encore de note, on insère
            String insertSql = "INSERT INTO article_rating (user_id, article_id, rating, date_rating) VALUES (?, ?, ?, NOW())";
            PreparedStatement insertStmt = connection.prepareStatement(insertSql);
            insertStmt.setInt(1, idUtilisateur);
            insertStmt.setInt(2, idArticle);
            insertStmt.setInt(3, note);
            insertStmt.executeUpdate();
            System.out.println("🆕 Nouveau rating ajouté !");
        }
    }


    public double getMoyenneRating(int idArticle) throws SQLException {
        String sql = "SELECT AVG(rating) FROM article_rating WHERE article_id = ?";
       // PreparedStatement pst = .getInstance().getCnx().prepareStatement(sql);
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, idArticle);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getDouble(1);
        }
        return 0;
    }


    public void incrementerVues(int articleId) throws SQLException {
        String query = "UPDATE article SET nbre_vue = nbre_vue + 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, articleId);
            stmt.executeUpdate();
        }
    }





    public int getNombreVuesById(int articleId) throws SQLException {
        String query = "SELECT nbre_vue FROM article WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, articleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("nbre_vue");
            }
        }
        return 0;
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
                a.setNbreVue(rs.getInt("nbre_vue"));
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

