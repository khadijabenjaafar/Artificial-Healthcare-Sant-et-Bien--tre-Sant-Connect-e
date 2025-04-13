package com.example.test;



import com.example.entities.Article;
import com.example.entities.Commentaire;
import com.example.entities.Utilisateur;
import com.example.services.ServiceCommentaire;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class TestCommentaireConsole {
    public static void main(String[] args) throws SQLException {

        ServiceCommentaire service = new ServiceCommentaire();

        // ✅ Créer un utilisateur et un article fictifs (avec IDs existants en base)
        Utilisateur user = new Utilisateur();
        user.setId(1); // Remplace si besoin

        Article article = new Article();
        article.setId(1); // Remplace aussi si besoin

        //  Créer un commentaire
        Commentaire commentaire = new Commentaire();
        commentaire.setContenue(" Ceci est un commentaire de test");
        commentaire.setDateCommentaire(Date.valueOf("2025-04-09"));
        commentaire.setStatus(true);
        commentaire.setHeure(Time.valueOf("14:30:00"));
        commentaire.setParentId(null); // ou un ID si c’est une réponse
        commentaire.setRating(5);
        commentaire.setLikes(10);
        commentaire.setDislikes(2);
        commentaire.setUtilisateur(user);
        commentaire.setArticle(article);


        try{
        //  Ajouter
       // service.ajouter(commentaire);
        //System.out.println(" Commentaire ajouté !");

        //  Afficher
        //List<Commentaire> commentaires = service.afficher();
        //for (Commentaire c : commentaires) {
          //  System.out.println("🗨️ ID: " + c.getIdCommentaire() + " | Contenu: " + c.getContenue());
        //}

        // ️ Modifier un commentaire existant
        int idAModifier = 1; // mets l'ID à modifier ici
        Commentaire commentaireModif = new Commentaire();
        commentaireModif.setIdCommentaire(idAModifier);
        commentaireModif.setContenue(" Commentaire modifié !");
        commentaireModif.setDateCommentaire(Date.valueOf("2025-04-10"));
        commentaireModif.setStatus(false);
        commentaireModif.setHeure(Time.valueOf("15:00:00"));
        commentaireModif.setParentId(null);
        commentaireModif.setRating(4);
        commentaireModif.setLikes(20);
        commentaireModif.setDislikes(1);
        commentaireModif.setUtilisateur(user);
        commentaireModif.setArticle(article);

        //service.modifier(commentaireModif);
        //System.out.println("✏ Commentaire modifié !");

        // ️ Supprimer un commentaire
        int idASupprimer = 2; // mets l'ID à supprimer ici
        //service.supprimer(idASupprimer);
        //System.out.println("🗑️ Commentaire supprimé !");

        // 📋 Affichage final
        System.out.println("\n📋 Commentaires restants :");
        List<Commentaire> finalCommentaires = service.afficher();
        for (Commentaire c : finalCommentaires) {
            System.out.println("🔎 ID: " + c.getIdCommentaire() + " | Contenu: " + c.getContenue()+" ecrit par "+c.getUtilisateur().getNom() +"SUR ARTICLE"+c.getArticle().getId());
        }}
        catch (Exception e) {
            System.out.println(" Erreur SQL : " + e.getMessage());
        }
    }
}

