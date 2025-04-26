package org.example.services;


import org.example.entities.Article;
import org.example.entities.Commentaire;
import org.example.entities.Utilisateur;
import org.example.services.IService;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ServiceCommentaire implements IService<Commentaire> {
    private Connection connection;


    public ServiceCommentaire() {
        connection = MyDataBase.getInstance().getMyConnection();
    }
    @Override
    public void ajouter(Commentaire commentaire) throws SQLException {
        String sql = "INSERT INTO commentaire (contenue, date_commentaire, status, heure, parent_id, rating, likes, dislikes, id_utilisateur, article_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, commentaire.getContenue());
        pst.setDate(2, commentaire.getDateCommentaire());
        pst.setBoolean(3, commentaire.isStatus());
        pst.setTime(4, commentaire.getHeure());
        pst.setObject(5, commentaire.getParentId(), Types.INTEGER);
        pst.setObject(6, commentaire.getRating(), Types.INTEGER);
        pst.setInt(7, commentaire.getLikes());
        pst.setInt(8, commentaire.getDislikes());
        pst.setInt(9, commentaire.getUtilisateur().getId());
        pst.setInt(10, commentaire.getArticle().getId());
        pst.executeUpdate();

    }



    @Override
    public void modifier(Commentaire commentaire) throws SQLException {
        String sql = "UPDATE commentaire SET contenue = ?, date_commentaire = ?, status = ?, heure = ?, parent_id = ?, rating = ?, likes = ?, dislikes = ?, id_utilisateur = ?, article_id = ? WHERE id_commentaire = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, commentaire.getContenue());
        pst.setDate(2, commentaire.getDateCommentaire());
        pst.setBoolean(3, commentaire.isStatus());
        pst.setTime(4, commentaire.getHeure());
        pst.setObject(5, commentaire.getParentId(), Types.INTEGER);
        pst.setObject(6, commentaire.getRating(), Types.INTEGER);
        pst.setInt(7, commentaire.getLikes());
        pst.setInt(8, commentaire.getDislikes());
        pst.setInt(9, commentaire.getUtilisateur().getId());
        pst.setInt(10, commentaire.getArticle().getId());
        pst.setInt(11, commentaire.getIdCommentaire());
        pst.executeUpdate();

    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM commentaire WHERE id_commentaire = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();

    }

    @Override
    public List<Commentaire> afficher() throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        String sql = "SELECT * FROM commentaire WHERE id_article = ? ORDER BY date_commentaire DESC";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Commentaire c = new Commentaire();
            c.setIdCommentaire(rs.getInt("id_commentaire"));
            c.setContenue(rs.getString("contenue"));
            c.setDateCommentaire(rs.getDate("date_commentaire"));
            c.setStatus(rs.getBoolean("status"));
            c.setHeure(rs.getTime("heure"));
            c.setParentId(rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null);
            c.setRating(rs.getObject("rating") != null ? rs.getInt("rating") : null);
            c.setLikes(rs.getInt("likes"));
            c.setDislikes(rs.getInt("dislikes"));

            Utilisateur u = new Utilisateur();
            u.setId(rs.getInt("id_utilisateur"));
            c.setUtilisateur(u);

            Article a = new Article();
            a.setId(rs.getInt("article_id"));
            c.setArticle(a);

            commentaires.add(c);
        }

        return commentaires;
    }
    public List<Commentaire> getCommentairesByArticle(int idArticle) {
        List<Commentaire> list = new ArrayList<>();
        String sql = "SELECT c.*, u.nom, u.prenom\n" +
                "FROM commentaire c\n" +
                "JOIN utilisateur u ON c.id_utilisateur = u.id\n" +
                "WHERE c.article_id = ?\n" +
                "ORDER BY c.date_commentaire DESC, c.heure DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idArticle);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Commentaire c = new Commentaire();
                c.setIdCommentaire(rs.getInt("id_commentaire"));
                c.setContenue(rs.getString("contenue"));
                c.setDateCommentaire(rs.getDate("date_commentaire"));
                c.setHeure(rs.getTime("heure"));
                Utilisateur u = new Utilisateur();
                Article a = new Article();
                a.setId(rs.getInt("article_id"));
                c.setArticle(a);
                u.setId(rs.getInt("id_utilisateur"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                c.setUtilisateur(u); // lier à commentaire

                // ajoute les autres champs si besoin
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }





    }

