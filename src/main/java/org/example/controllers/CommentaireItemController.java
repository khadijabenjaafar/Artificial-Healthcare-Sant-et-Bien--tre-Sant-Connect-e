package org.example.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.entities.Commentaire;

import java.text.SimpleDateFormat;

public class CommentaireItemController {


    @FXML
    private Label nomUtilisateurLabel;

    @FXML
    private Label contenuLabel;

    @FXML
    private Label dateLabel;

    public void setCommentaire(Commentaire commentaire) {
        if (commentaire.getUtilisateur() != null) {
            nomUtilisateurLabel.setText(commentaire.getUtilisateur().getPrenom() + " " + commentaire.getUtilisateur().getNom());
        } else {
            nomUtilisateurLabel.setText("Utilisateur inconnu");
        }

        contenuLabel.setText(commentaire.getContenue());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String date = "";
        if (commentaire.getDateCommentaire() != null && commentaire.getHeure() != null) {
            date = sdf.format(commentaire.getDateCommentaire()) + " " + commentaire.getHeure().toLocalTime().toString();
        }
        dateLabel.setText(date);
    }
}