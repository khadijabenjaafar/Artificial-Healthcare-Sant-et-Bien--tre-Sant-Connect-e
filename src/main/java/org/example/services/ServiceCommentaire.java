
package org.example.services;


import javafx.scene.control.Alert;
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
        if (commentaire.getParentId() == 0) {
            pst.setNull(5,commentaire.getParentId()); // commentaire principal
        } else {
            pst.setInt(5, commentaire.getParentId()); // sous-commentaire
        }
       // pst.setObject(5, commentaire.getParentId());
        pst.setObject(6, commentaire.getRating());
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
        if (commentaire.getParentId() == 0) {
            pst.setNull(5,commentaire.getParentId()); // commentaire principal
        } else {
            pst.setInt(5, commentaire.getParentId()); // sous-commentaire
        }
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
                c.setParentId(rs.getInt("parent_id"));
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



    public List<Commentaire> getReponses(int parentId) {
        List<Commentaire> reponses = new ArrayList<>();
        String sql = "SELECT * FROM commentaire WHERE parent_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, parentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Commentaire c = mapToCommentaire(rs);
                reponses.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reponses;
    }



    private Commentaire mapToCommentaire(ResultSet rs) throws SQLException {
        Commentaire commentaire = new Commentaire();
        commentaire.setIdCommentaire(rs.getInt("id_commentaire"));
        commentaire.setContenue(rs.getString("contenue"));
        commentaire.setDateCommentaire(rs.getDate("date_commentaire"));
        commentaire.setHeure(rs.getTime("heure"));
        commentaire.setParentId(rs.getInt("parent_id"));

        // Si tu as stocké utilisateur_id, récupère aussi l'utilisateur
        int utilisateurId = rs.getInt("utilisateur_id");
        Utilisateur utilisateur = new ServiceUtilisateur().getById(utilisateurId);
        commentaire.setUtilisateur(utilisateur);

        // Si tu as besoin aussi de l’article
        int articleId = rs.getInt("article_id");
        Article article = new Article();
        article.setId(articleId); // ou récupérer tout l’article si tu veux
        commentaire.setArticle(article);

        return commentaire;
    }



    public void signalerCommentaire(int commentaireId, int utilisateurId) throws SQLException {
        if (aDejaSignale(commentaireId, utilisateurId)) {
            System.out.println("⚠️ Ce commentaire a déjà été signalé par cet utilisateur.");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Déjà signalé");
            alert.setHeaderText(null);
            alert.setContentText("Ce commentaire a déjà été signalé par cet utilisateur.");
            alert.showAndWait();
            return;
        }

        String insert = "INSERT INTO commentaire_signalement (commentaire_id, id_utilisateur) VALUES (?, ?)";
        PreparedStatement pst = connection.prepareStatement(insert);
        pst.setInt(1, commentaireId);
        pst.setInt(2, utilisateurId);
        pst.executeUpdate();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Commentaire signalé");
        alert.setHeaderText(null);
        alert.setContentText("🗑 Commentaire a été signalé ");
        alert.showAndWait();

        // Vérifier le nombre de signalements
        String count = "SELECT COUNT(*) FROM commentaire_signalement WHERE commentaire_id = ?";
        PreparedStatement pst2 = connection.prepareStatement(count);
        pst2.setInt(1, commentaireId);
        ResultSet rs = pst2.executeQuery();
        if (rs.next() && rs.getInt(1) >= 3) {
            String delete = "DELETE FROM commentaire WHERE id_commentaire = ?";
            PreparedStatement pst3 = connection.prepareStatement(delete);
            pst3.setInt(1, commentaireId);
            pst3.executeUpdate();
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Déjà signalé");
            alert1.setHeaderText(null);
            alert1.setContentText("🗑 Commentaire supprimé automatiquement après 3 signalements..");
            alert1.showAndWait();




        }
    }


    public boolean aDejaSignale(int commentaireId, int utilisateurId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM commentaire_signalement WHERE commentaire_id = ? AND id_utilisateur = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, commentaireId);
        pst.setInt(2, utilisateurId);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }



    public void clicLike(int commentaireId, int userId, boolean isLike) throws SQLException {
        String selectSql = "SELECT vote_type FROM commentaire_vote WHERE commentaire_id = ? AND id_utilisateur = ?";
        PreparedStatement selectStmt = connection.prepareStatement(selectSql);
        selectStmt.setInt(1, commentaireId);
        selectStmt.setInt(2, userId);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            String current = rs.getString("vote_type");
            if ((isLike && current.equals("like")) || (!isLike && current.equals("dislike"))) {
                // Supprimer l'existant si re-cliqué sur le même
                PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM commentaire_vote WHERE commentaire_id = ? AND id_utilisateur = ?");
                deleteStmt.setInt(1, commentaireId);
                deleteStmt.setInt(2, userId);
                deleteStmt.executeUpdate();
            } else {
                // Mettre à jour le type (changer de like à dislike ou l’inverse)
                PreparedStatement updateStmt = connection.prepareStatement("UPDATE commentaire_vote SET vote_type = ? WHERE commentaire_id = ? AND id_utilisateur = ?");
                updateStmt.setString(1, isLike ? "like" : "dislike");
                updateStmt.setInt(2, commentaireId);
                updateStmt.setInt(3, userId);
                updateStmt.executeUpdate();
            }
        } else {
            // Ajouter un nouveau like ou dislike
            PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO commentaire_vote (commentaire_id, id_utilisateur, vote_type) VALUES (?, ?, ?)");
            insertStmt.setInt(1, commentaireId);
            insertStmt.setInt(2, userId);
            insertStmt.setString(3, isLike ? "like" : "dislike");
            insertStmt.executeUpdate();
        }
    }

    public int countLikes(int commentaireId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM commentaire_vote WHERE commentaire_id = ? AND vote_type = 'like'";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, commentaireId);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }

    public int countDislikes(int commentaireId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM commentaire_vote WHERE commentaire_id = ? AND vote_type = 'dislike'";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, commentaireId);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }





    }


