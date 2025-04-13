package com.example.test;



import com.example.entities.Article;
import com.example.entities.Utilisateur;
import com.example.services.ServiceArticle;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class TestArticleConsole {

    public static void main(String[] args) {

        ServiceArticle service = new ServiceArticle();

        // Créer un utilisateur fictif avec un ID existant dans la base
        Utilisateur user = new Utilisateur();
        user.setId(1); // ⚠️ Remplace 1 par un ID existant

        Article article = new Article();
        article.setContenue("Article de test depuis console ✨");
        article.setDateArticle(Date.valueOf("2025-04-09"));
        article.setNbreVue(100);
        article.setUtilisateur(user);

        try {
            // Ajouterx
            //service.ajouter(article);
            //System.out.println(" Article ajouté !");



            // ✏️ Modifier l’article (remplace par un ID réel dans ta base)
            int idAModifier = 8; // <--- ⚠️ mets ici l’ID à modifier
            Article articleModif = new Article();
            articleModif.setId(idAModifier);
            articleModif.setContenue("📝 Article modifié !");
            articleModif.setDateArticle(Date.valueOf("2025-04-10"));
            articleModif.setNbreVue(200);
            articleModif.setUtilisateur(user);

            service.modifier(articleModif);
            System.out.println("✏️ Article modifié.");




            // 🗑️ Supprimer un article (remplace par l’ID que tu veux supprimer)
           // int idASupprimer = 9; // <--- ⚠️ mets ici l’ID à supprimer
            //service.supprimer(idASupprimer);
            //System.out.println("🗑️ Article supprimé.");

            // Afficher
            List<Article> articles = service.afficher();
            for (Article a : articles) {
                System.out.println(" ID: " + a.getId() + " | Contenu: " + a.getContenue());
            }

        } catch (SQLException e) {
            System.out.println(" Erreur SQL : " + e.getMessage());
            }
    }
}
