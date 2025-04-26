package org.example.controllers;

import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.example.entities.Article;
import org.example.entities.Commentaire;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import org.example.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;

import static org.example.services.ResumeService.couperResume;
//import org.example.services.TranslationServiceSimple;

public class ArticledetailsController {
    public Utilisateur CurrentUser = UserConnecter.getInstance().getUserConnecter();


    @FXML
    private Label labelTitre;

    @FXML
    private ImageView imageArticle;

    @FXML
    private Label contenuArticle;


    @FXML
    private Label dateArticle;

    @FXML
    private TextFlow contenuTextFlow;

    @FXML
    private VBox traductionContainer;


    @FXML
    private VBox BoxVoice;


    // @FXML
    //private ComboBox<Utilisateur> comboUtilisateur;


    @FXML
    private VBox commentairesContainer;

    @FXML
    private Label errorCommentaire;

    @FXML
    private TextField champCommentaire;

    private Article article;

    private final ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    private Pane contentPane;

    @FXML
    private HBox ratingBox;
    @FXML
    private HBox partageBox;

    @FXML
    private HBox resumeBox ;

    @FXML
    private ComboBox<String> langueComboBox;

    List<String> badWords = List.of("merde", "con", "idiot", "pute", "fuck", "homphobe", "nique", "ta mère", "ntm", "gros con", "bâtard", "pédé", "fils de pute", "abruti", "débile", "emmerdeur", "trou du cul",
            "fuck", "shit", "bitch", "asshole", "bastard", "motherfucker", "dick", "cunt", "crap", "suck", "fag", "jerk"
    );


    ServiceArticle serviceArticle = new ServiceArticle();

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;


    // Appelée automatiquement quand le FXML est chargé
    @FXML
    private void initialize() {


        // ComboBox<String> langueComboBox = new ComboBox<>()
        // Pas besoin de faire quoi que ce soit ici si on n'a pas encore reçu l'article
    }


    public void setContentPane(Pane contentPane) {
        this.contentPane = contentPane;
    }


    // Méthode appelée depuis ArticleItemController
    public void setArticle(Article article) {
        this.article = article;


        // Vérifie que les éléments FXML ne sont pas nuls avant de les utiliser
        if (labelTitre != null && contenuTextFlow != null && dateArticle != null) {
            labelTitre.setText(article.getTitre());

            contenuTextFlow.getChildren().clear();
            Text contenu = new Text(article.getContenue());
            contenu.setWrappingWidth(560);
            contenu.setStyle("-fx-font-size: 14px;");
            contenuTextFlow.getChildren().add(contenu);

            dateArticle.setText(article.getDateArticle().toString());

            if (article.getUrlimagearticle() != null) {
                File file = new File(article.getUrlimagearticle());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageArticle.setImage(image);
                } else {
                    System.out.println("Image not found: " + file.getAbsolutePath());
                }
            }
        }
//afficher le rating
        try {
            afficherRating(article.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //ahouter les vues
        try {
            ServiceArticle serviceArticle = new ServiceArticle();
            serviceArticle.incrementerVues(article.getId());
            System.out.println(article.getNbreVue());
            System.out.println(article.getId());
        } catch (SQLException e) {
            System.out.println("failed");
            e.printStackTrace();
        }


        if (article.getContenue() != null && !article.getContenue().isEmpty()) {
            // 1. Générer l’audio
            TextToSpeech.speak(article.getContenue(), "article_audio.mp3");

            // 2. Créer l'interface du lecteur
            File file = new File("article_audio.mp3");
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            Button playPauseButton = new Button("▶️");
            Slider progressSlider = new Slider();
            progressSlider.setMin(0);
            progressSlider.setPrefWidth(200);
            Label durationLabel = new Label("00:00");

            HBox playerBox = new HBox(10, playPauseButton, progressSlider, durationLabel);
            playerBox.setStyle("-fx-alignment: center; -fx-padding: 10;");

            BoxVoice.getChildren().add(playerBox); // ajouter au layout

            // 3. Préparer le MediaPlayer
            mediaPlayer.setOnReady(() -> {
                Duration total = mediaPlayer.getTotalDuration();
                progressSlider.setMax(total.toSeconds());
                durationLabel.setText(formatTime(total.toSeconds()));
            });

            // 4. Animation de suivi de lecture
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (mediaPlayer != null && isPlaying) {
                        progressSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                    }
                }
            };
            timer.start();

            // 5. Bouton Play/Pause
            playPauseButton.setOnAction(e -> {
                if (isPlaying) {
                    mediaPlayer.pause();
                    playPauseButton.setText("▶️");
                } else {
                    mediaPlayer.play();
                    playPauseButton.setText("⏸️");
                }
                isPlaying = !isPlaying;
            });
        }



        String contenu = article.getContenue();
        String resume = ResumeService.resumerArticle(contenu);
       // String resumeCourt = couperResume(resume, 50);
      //
        // System.out.println("resume =" + resume);


        // Créer le Label de Résumé
        Label resumeLabel = new Label(resume);
        resumeLabel.setWrapText(true);
        resumeLabel.setMaxWidth(800);
        resumeLabel.setMinHeight(Region.USE_PREF_SIZE);
       // resumeLabel.setMaxHeight(Region.USE_PREF_SIZE);
        resumeLabel.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20; -fx-background-radius: 10; -fx-font-size: 14px;");

// Créer un titre "Résumé :"
        Label titreResume = new Label("Résumé :");
        titreResume.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");
        titreResume.setPadding(new Insets(0, 0, 10, 0));

// Wrapper pour centrer proprement
        HBox wrapper1 = new HBox(resumeLabel);
        wrapper1.setAlignment(Pos.CENTER);

// Maintenant ajouter dans resumeBox proprement
        resumeBox.getChildren().clear(); // si tu veux refresh à chaque fois
        resumeBox.getChildren().addAll(titreResume, wrapper1);












        Button fbButton = new Button("Facebook");
        Button twButton = new Button("Twitter");
        Button lnButton = new Button("LinkedIn");
        Button igButton = new Button("Instagram");

// Actions
        fbButton.setOnAction(e -> partagerSurReseau("facebook", article));
        twButton.setOnAction(e -> partagerSurReseau("twitter", article));
        lnButton.setOnAction(e -> partagerSurReseau("linkedin", article));
        igButton.setOnAction(e -> partagerSurReseau("instagram", article));

// Style
        fbButton.setStyle("-fx-background-color: #3b5998; -fx-text-fill: white;");
        twButton.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white;");
        lnButton.setStyle("-fx-background-color: #0077b5; -fx-text-fill: white;");
        igButton.setStyle("-fx-background-color: #E1306C; -fx-text-fill: white;");

        HBox shareBox = new HBox(10, fbButton, twButton, lnButton, igButton);
        shareBox.setAlignment(Pos.CENTER_LEFT);
        shareBox.setPadding(new Insets(10, 0, 10, 0));


        partageBox.setAlignment(Pos.CENTER);
        partageBox.getChildren().add(0, shareBox);


        // ComboBox<String> langueComboBox = new ComboBox<>();
       // langueComboBox.getItems().addAll("Français", "Arabe", "Espanol");
       // langueComboBox.setPromptText("Choisir une langue");
       // langueComboBox.setStyle("-fx-font-size: 14px; -fx-pref-width: 200;");

        langueComboBox.getItems().addAll("Français", "Arabe", "Espagnol");
        langueComboBox.setPromptText("Choisir une langue");
        langueComboBox.setStyle("-fx-font-size: 14px; -fx-pref-width: 200;");

// Map langues -> codes ISO
        Map<String, String> languageCodes = new HashMap<>();
        languageCodes.put("Espagnol", "es");
        languageCodes.put("Français", "fr");
        languageCodes.put("Arabe", "ar");

// Gestion de la sélection
        langueComboBox.setOnAction(event -> {
            String langueChoisie = langueComboBox.getValue();
            String codeLangue = languageCodes.get(langueChoisie);

            if (codeLangue != null) {
                String contenuOriginal = article.getContenue();
                String contenuTraduit = TranslationServiceSimple.traduireTexteLong(contenuOriginal, codeLangue);

                Label labelTraduction = new Label("Traduction en " + langueChoisie + " :");
                labelTraduction.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

                Label contenuTraduitLabel = new Label(contenuTraduit);
                contenuTraduitLabel.setWrapText(true);
                contenuTraduitLabel.setMaxWidth(700);
                contenuTraduitLabel.setMinHeight(Region.USE_PREF_SIZE);
                contenuTraduitLabel.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20; -fx-background-radius: 10;");

                HBox wrapper = new HBox(contenuTraduitLabel);
                wrapper.setAlignment(Pos.CENTER);

                traductionContainer.getChildren().clear();
                traductionContainer.getChildren().addAll(langueComboBox, labelTraduction, wrapper);
            }
        });














        // String contenuOriginal = article.getContenue();
        //  String contenuTraduit = TranslationServiceSimple.traduire(contenuOriginal, "en", "fr");
        //  String contenuLisible = decodeUnicode(contenuTraduit);
        // decodeUnicode(contenuTraduit);
        // TextToSpeech.speak(contenuOriginal, "article_audio.mp3");


        //Label labelTraduction = new Label("Traduction en anglais :");
        // labelTraduction.setStyle("-fx-font-weight: bold; -fx-text-fill: #555;");

        // Label contenuTraduitLabel = new Label(contenuLisible);
        // contenuTraduitLabel.setWrapText(true);
        // contenuTraduitLabel.setMaxWidth(700);
        // contenuTraduitLabel.setMinHeight(Region.USE_PREF_SIZE);

        // contenuTraduitLabel.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20; -fx-background-radius: 10;");
        //  traductionContainer.setAlignment(Pos.CENTER); // centrer tous les enfants
        // traductionContainer.getChildren().clear(); // Nettoyer avant d'ajouter
        //  traductionContainer.getChildren().addAll(labelTraduction, contenuTraduitLabel);


        afficherCommentaires();


    }


    private String formatTime(double seconds) {
        int minutes = (int) seconds / 60;
        int secs = (int) seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }


    boolean contientBadWord(String texte) {
        for (String word : badWords) {
            if (texte.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }


    private void afficherRating(int articleId) throws SQLException {
        ratingBox.getChildren().clear();
        ratingBox.setSpacing(8);
        ratingBox.setAlignment(Pos.CENTER);
        double moyenne = serviceArticle.getMoyenneRating(articleId);
        int fullStars = (int) moyenne;
        Label moyenneLabel = new Label("★ Moyenne : " + String.format("%.1f", moyenne));
        moyenneLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
        ratingBox.getChildren().add(moyenneLabel);

        for (int i = 1; i <= 5; i++) {
            Label star = new Label(i <= fullStars ? "★" : "☆");
            int note = i;
            String baseStyle = "-fx-font-size: 22px; -fx-cursor: hand; ";
            // Couleur conditionnelle
            if (i <= moyenne) {
                baseStyle += "-fx-text-fill: #FFD700;"; // Gold
            } else {
                baseStyle += "-fx-text-fill: #ccc;"; // Grey
            }
            star.setStyle(baseStyle);
            // star.setStyle("-fx-font-size: 20px; );
            star.setOnMouseClicked(e -> {
                try {
                    serviceArticle.ajouterOuMettreAJourRating(CurrentUser.getId(), articleId, note);
                    afficherRating(articleId); // Rafraîchir
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
            ratingBox.getChildren().add(star);
            // Label moyenneLabel = new Label("⭐ Moyenne : " + String.format("%.1f", moyenne));
            // ratingBox.getChildren().add(moyenneLabel);
        }
    }


    @FXML
    private void envoyerCommentaire() {
        String texte = champCommentaire.getText().trim();
        if (texte.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champ vide");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez écrire un commentaire avant de l'envoyer.");
            alert.showAndWait();
            return;
        }

        if (contientBadWord(texte)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Ce commentaire contient des mots interdits !", ButtonType.OK);
            alert.showAndWait();
        } else {
            Commentaire c = new Commentaire();
            c.setContenue(texte);
            c.setUtilisateur(CurrentUser);
            c.setArticle(article); // 💡 lien avec l'article affiché
            c.setDateCommentaire(Date.valueOf(LocalDate.now()));
            c.setHeure(Time.valueOf(LocalTime.now()));
            //  c.setDateCommentaire(Date.now());

            ServiceCommentaire service = new ServiceCommentaire();
            try {
                service.ajouter(c); //  Enregistrer en base
                champCommentaire.clear();
                System.out.println("Commentaire enregistré !");
                afficherCommentaires();
                champCommentaire.clear();
                System.out.println("Commentaire enregistré !");

                // Optionnel : rafraîchir l’affichage
                afficherCommentaires();// pour rafraîchir les commentaires affichés
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de l'ajout du commentaire.");
            }


        }
    }


    private void afficherCommentaires() {
        commentairesContainer.getChildren().clear();
        ServiceCommentaire serviceCommentaire = new ServiceCommentaire();

        // Obtenir TOUS les commentaires de l’article
        List<Commentaire> tous = serviceCommentaire.getCommentairesByArticle(article.getId());

        // Afficher uniquement les commentaires PARENTS d’abord
        for (Commentaire commentaire : tous) {
            if (commentaire.getParentId() == 0) {
                VBox commentaireBox = creerCommentaireAvecReponses(commentaire, tous, 0); // indentation 0 pour parent
                commentairesContainer.getChildren().add(commentaireBox);
            }
        }
    }


    private VBox creerCommentaireAvecReponses(Commentaire parent, List<Commentaire> tous, int indentation) {
        VBox bloc = new VBox();
        bloc.setSpacing(5);
        bloc.setPadding(new Insets(10, 0, 0, indentation)); // indentation dynamique


        // Infos du commentaire
        Label labelNom = new Label(parent.getUtilisateur().getPrenom() + " " + parent.getUtilisateur().getNom());
        labelNom.setStyle("-fx-font-weight: bold;");

        Label contenu = new Label(parent.getContenue());
        contenu.setWrapText(true);
        contenu.setStyle("-fx-background-color: #E8EAF6; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label date = new Label(parent.getDateCommentaire() + " à " + parent.getHeure().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        date.setStyle("-fx-text-fill: #777; -fx-font-size: 10px;");


        MenuButton menuButton = new MenuButton("⋮");
        menuButton.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;-fx-text-fill: #555;");

        MenuItem repondre = new MenuItem("Répondre");
        MenuItem modifier = new MenuItem("Modifier");
        MenuItem supprimer = new MenuItem("Supprimer");
        MenuItem signaler = new MenuItem("Signaler");


        if (parent.getUtilisateur().getId() == CurrentUser.getId()) {
            menuButton.getItems().addAll(modifier, supprimer);
        } else {
            menuButton.getItems().addAll(repondre, signaler);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(spacer, menuButton);
        topBar.setAlignment(Pos.CENTER_RIGHT);

        // menuButton.getItems().addAll(repondre, modifier, supprimer, signaler);

        VBox box = new VBox(labelNom, topBar, contenu, date);
        box.setSpacing(5);
        bloc.setStyle(
                "-fx-border-color: #ccc;" +              // Bordure grise claire
                        "-fx-border-width: 1;" +                 // Épaisseur de 1px
                        "-fx-border-radius: 10;" +               // Coins arrondis
                        "-fx-background-color: #fdfdfd;" +       // Fond blanc cassé
                        "-fx-padding: 10;"                       // Espace intérieur
        );

        bloc.getChildren().add(box);


        signaler.setOnAction(e -> {
            try {
                new ServiceCommentaire().signalerCommentaire(parent.getIdCommentaire(), CurrentUser.getId());
                //Alert alert = new Alert(Alert.AlertType.INFORMATION);
                //alert.setTitle("Signalement");
                //alert.setHeaderText(null);
                //alert.setContentText("Le commentaire a été signalé.");
                //alert.showAndWait();
                afficherCommentaires(); // 🔄 refresh après suppression potentielle
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });


        modifier.setOnAction(e -> {
            TextField champModif = new TextField(parent.getContenue());
            champModif.setPrefWidth(400);
            Button btnSave = new Button("💾 Modifier");
            btnSave.setStyle(
                    "-fx-background-color: #4CAF50;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 15;" +
                            "-fx-padding: 5 20;" +
                            "-fx-cursor: hand;"
            );

            btnSave.setOnAction(ev -> {
                parent.setContenue(champModif.getText());
                try {
                    new ServiceCommentaire().modifier(parent);
                    afficherCommentaires();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            box.getChildren().setAll(labelNom, champModif, btnSave, date);
        });


        supprimer.setOnAction(e -> {
            try {
                new ServiceCommentaire().supprimer(parent.getIdCommentaire());
                afficherCommentaires();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // Gestion de réponse directe
        repondre.setOnAction(e -> {
            TextField champ = new TextField();
            champ.setPromptText("Votre réponse...");
            Button envoyer = new Button("Envoyer");
            envoyer.setStyle(
                    "-fx-background-color: #4CAF50;" +  // vert doux
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 25;" +
                            "-fx-padding: 6 20;" +
                            "-fx-font-size: 13px;" +
                            "-fx-cursor: hand;"
            );


            envoyer.setOnAction(ev -> {
                if (!champ.getText().trim().isEmpty()) {
                    if (contientBadWord(champ.getText().trim())) {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Ce commentaire contient des mots interdits !", ButtonType.OK);
                        alert.showAndWait();
                        return;
                    } else {
                        Commentaire reponse = new Commentaire();
                        reponse.setContenue(champ.getText().trim());
                        reponse.setUtilisateur(CurrentUser);
                        reponse.setArticle(article);
                        reponse.setParentId(parent.getIdCommentaire());
                        reponse.setDateCommentaire(Date.valueOf(LocalDate.now()));
                        reponse.setHeure(Time.valueOf(LocalTime.now()));

                        try {
                            new ServiceCommentaire().ajouter(reponse);
                            afficherCommentaires(); // on recharge tout
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });


            bloc.getChildren().add(new VBox(5, champ, envoyer));
        });


        Label likeLabel = new Label("👍 0");
        Label dislikeLabel = new Label("👎 0");

        try {
            likeLabel.setText("👍 " + new ServiceCommentaire().countLikes(parent.getIdCommentaire()));
            dislikeLabel.setText("👎 " + new ServiceCommentaire().countDislikes(parent.getIdCommentaire()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        likeLabel.setOnMouseClicked(e -> {
            try {
                new ServiceCommentaire().clicLike(parent.getIdCommentaire(), CurrentUser.getId(), true);
                afficherCommentaires();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        dislikeLabel.setOnMouseClicked(e -> {
            try {
                new ServiceCommentaire().clicLike(parent.getIdCommentaire(), CurrentUser.getId(), false);
                afficherCommentaires();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        HBox votesBox = new HBox(10, likeLabel, dislikeLabel);
        votesBox.setAlignment(Pos.CENTER_LEFT);
        bloc.getChildren().add(votesBox);


        // 🔄 Afficher les réponses à ce commentaire (indentées +20px)
        for (Commentaire c : tous) {
            if (parent.getIdCommentaire() == (c.getParentId())) {
                VBox reponseBox = creerCommentaireAvecReponses(c, tous, indentation + 20);
                reponseBox.setPadding(new Insets(5, 0, 5, 30));
                bloc.getChildren().add(reponseBox);
            }
        }

        return bloc;
    }


    private void partagerSurReseau(String plateforme, Article article) {
        try {
            String titre = article.getTitre();
            String urlArticle = "https://tonsite.com/article/" + article.getId(); // Remplace avec le vrai lien

            String shareUrl = switch (plateforme.toLowerCase()) {
                case "facebook" -> "https://www.facebook.com/sharer/sharer.php?u=" + urlArticle;
                case "twitter" -> "https://twitter.com/intent/tweet?text=" + titre + "&url=" + urlArticle;
                case "linkedin" -> "https://www.linkedin.com/sharing/share-offsite/?url=" + urlArticle;
                case "instagram" -> null; // Instagram ne supporte pas le partage par lien web
                default -> null;
            };

            if (shareUrl != null) {
                Desktop.getDesktop().browse(new URI(shareUrl));
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Le partage vers Instagram doit se faire manuellement.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String decodeUnicode(String unicodeText) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < unicodeText.length()) {
            char c = unicodeText.charAt(i++);
            if (c == '\\' && i < unicodeText.length() && unicodeText.charAt(i) == 'u') {
                i++;
                int code = Integer.parseInt(unicodeText.substring(i, i + 4), 16);
                result.append((char) code);
                i += 4;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }


}























