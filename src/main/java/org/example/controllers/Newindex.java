    package org.example.controllers;

    import javafx.animation.KeyFrame;
    import javafx.animation.ScaleTransition;
    import javafx.animation.Timeline;
    import javafx.application.Platform;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Node;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Alert;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.image.ImageView;
    import javafx.scene.input.MouseEvent;
    import javafx.scene.media.AudioClip;
    import javafx.stage.Stage;
    import javafx.util.Duration;
    import org.example.entities.*;
    import javafx.stage.Stage;
    import javafx.stage.StageStyle;
    import org.example.entities.EnumRole;
    import org.example.services.ServiceMessage;
    import org.example.services.ServiceNotification;

    import java.io.IOException;
    import java.sql.SQLException;
    import java.util.List;

    public class Newindex {
        @FXML
        private Label notificationBadge;

        @FXML
        private Button inscrire;

        @FXML
        private Button seConnecter;
        @FXML
        private Label chatBadge; // ✅ Label for badge

        @FXML
        private ImageView notificationIcon;
        private Timeline timeline; // ✅ Auto refresh

        @FXML
        private ImageView chatIcon;

        public Utilisateur CurrentUser = UserConnecter.getInstance().getUserConnecter();

        private Stage currentStage;

        public void setStage(Stage stage) {
            this.currentStage = stage;
        }


        @FXML
        public void initialize() {
            Platform.runLater(() -> {
                checkUserConnection();
                if (CurrentUser != null) {
                        checkUnreadMessages();
                    startAutoRefreshBadge();
                }
            });

        }

        @FXML
        void NavigateTosignUp(ActionEvent event) throws IOException {
            try {
                if (CurrentUser != null && CurrentUser.getRole() == EnumRole.ROLE_PATIENT) {
                    // Load the home.fxml file
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                    Parent root = loader.load();

                    // Get the current stage (window)
                    Stage stage = (Stage) inscrire.getScene().getWindow();

                    // Set the new scene with the home.fxml content
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    // Load the home.fxml file
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreerCompte.fxml"));
                    Parent root = loader.load();

                    // Get the current stage (window)
                    Stage stage = (Stage) inscrire.getScene().getWindow();

                    // Set the new scene with the home.fxml content
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }

            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        @FXML
        void NavigateTosignIn(ActionEvent event) throws SQLException, IOException {
            try {
                System.out.println("CurrentUser = " + CurrentUser);
                if (CurrentUser != null) {
                    System.out.println("Role = " + CurrentUser.getRole());
                }
                if (CurrentUser != null && CurrentUser.getRole() == EnumRole.ROLE_PATIENT) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
                    Parent parent = loader.load();
                    Profile profilControllers = loader.getController();
                    profilControllers.setNom(CurrentUser.getNom());
                    profilControllers.setPrenom(CurrentUser.getPrenom());
                    profilControllers.setAdresse(CurrentUser.getAdresse());
                    profilControllers.setEmail(CurrentUser.getEmail());
                    profilControllers.setImage1(CurrentUser.getImage1());


                    profilControllers.setGenre(CurrentUser.getGenre());
                    profilControllers.setNumtel(CurrentUser.getnumTel());
                    profilControllers.setDate(CurrentUser.getDate_naissance());

                    Stage stage = (Stage) seConnecter.getScene().getWindow();
                    Scene scene = new Scene(parent);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    // Redirige vers login.fxml si l'utilisateur n'est pas connecté
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) seConnecter.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @FXML
        void rendezVous(MouseEvent event) {

            if (CurrentUser == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Connexion requise");
                alert.setHeaderText(null);
                alert.setContentText("❗ Vous devez être connecté.");
                alert.showAndWait();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                    Parent loginView = loader.load();
                    Stage stage = (Stage) inscrire.getScene().getWindow();
                    Scene scene = new Scene(loginView);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CardRendezVous.fxml"));
                Parent root = loader.load();

                // Récupérer la fenêtre actuelle et changer la scène
                Stage stage = (Stage) inscrire.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void checkUserConnection() {
            if (CurrentUser != null && "ROLE_PATIENT".equals(CurrentUser.getRole().toString())) {
                inscrire.setText("Se deconnecter");
                seConnecter.setText("Profile");
            } else {
                inscrire.setText("S'inscrire");
                seConnecter.setText("Se connecter");
            }
        }

        @FXML
        void articles(MouseEvent event) {
            if (CurrentUser == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Connexion requise");
                alert.setHeaderText(null);
                alert.setContentText("❗ Vous devez être connecté.");
                alert.showAndWait();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                    Parent loginView = loader.load();
                    Stage stage = (Stage) inscrire.getScene().getWindow();
                    Scene scene = new Scene(loginView);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }


            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mesarticles.fxml"));
                Parent root = loader.load();

                // Récupérer la fenêtre actuelle et changer la scène
                Stage stage = (Stage) inscrire.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        void factures(MouseEvent event) {
            if (CurrentUser == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Connexion requise");
                alert.setHeaderText(null);
                alert.setContentText("❗ Vous devez être connecté.");
                alert.showAndWait();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                    Parent loginView = loader.load();
                    Stage stage = (Stage) inscrire.getScene().getWindow();
                    Scene scene = new Scene(loginView);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheFacturationP.fxml"));
                Parent root = loader.load();

                // Récupérer la fenêtre actuelle et changer la scène
                Stage stage = (Stage) inscrire.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        void freelancers(MouseEvent event) {
            if (CurrentUser == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Connexion requise");
                alert.setHeaderText(null);
                alert.setContentText("❗ Vous devez être connecté.");
                alert.showAndWait();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                    Parent loginView = loader.load();
                    Stage stage = (Stage) inscrire.getScene().getWindow();
                    Scene scene = new Scene(loginView);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/freelancer.fxml"));
                Parent root = loader.load();

                // Récupérer la fenêtre actuelle et changer la scène
                Stage stage = (Stage) inscrire.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        void ordonnances(MouseEvent event) {
            if (CurrentUser == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Connexion requise");
                alert.setHeaderText(null);
                alert.setContentText("❗ Vous devez être connecté.");
                alert.showAndWait();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                    Parent loginView = loader.load();
                    Stage stage = (Stage) inscrire.getScene().getWindow();
                    Scene scene = new Scene(loginView);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficheOrdonnanceP.fxml"));
                Parent root = loader.load();

                // Récupérer la fenêtre actuelle et changer la scène
                Stage stage = (Stage) inscrire.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        void apropos(MouseEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/indexFront.fxml"));
                Parent root = loader.load();

                // Récupérer la fenêtre actuelle et changer la scène
                Stage stage = (Stage) inscrire.getScene().getWindow(); // Assurez-vous que 'nom' est un contrôle valide
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        private void openChatWindow(ActionEvent event) {
            if (CurrentUser == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Connexion requise");
                alert.setHeaderText(null);
                alert.setContentText("❗ Vous devez être connecté.");
                alert.showAndWait();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                    Parent loginView = loader.load();
                    Stage stage = (Stage) inscrire.getScene().getWindow();
                    Scene scene = new Scene(loginView);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/chatbot.fxml"));
                Parent chatRoot = fxmlLoader.load();

                Stage chatStage = new Stage();
                chatStage.setTitle("Chatbot ClinicFlow");
                chatStage.setScene(new Scene(chatRoot));
                chatStage.setResizable(false);
                chatStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        private void openJitsiCall() {
            if (CurrentUser == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Connexion requise");
                alert.setHeaderText(null);
                alert.setContentText("❗ Vous devez être connecté.");
                alert.showAndWait();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                    Parent loginView = loader.load();
                    Stage stage = (Stage) inscrire.getScene().getWindow();
                    Scene scene = new Scene(loginView);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI("https://meet.jit.si/ClinicFlow"));
                System.out.println("Jitsi meeting opened in external browser!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void checkUnreadMessages() {
            if (CurrentUser == null) return;

            ServiceMessage serviceMessage = new ServiceMessage();
            int unreadCount = serviceMessage.getUnreadCount(CurrentUser.getId());

            Platform.runLater(() -> {
                boolean shouldShow = unreadCount > 0;
                if (shouldShow && !chatBadge.isVisible()) {
                    playPopAnimation(chatBadge); // 🔥 Bounce when new unread arrives
                }
                chatBadge.setVisible(shouldShow);
                chatBadge.setText(shouldShow ? String.valueOf(unreadCount) : "");
            });
        }

        @FXML
        private void handleChatClick() {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/chat.fxml"));
                Parent root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("Messagerie Instantanée");
                stage.setScene(new Scene(root));
                stage.show();

                chatBadge.setVisible(false); // ✅ When chat opens => reset badge
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @FXML
        private void handleNotificationClick() {
            try {
                ServiceNotification serviceNotification = new ServiceNotification();
                List<Notification> unreadNotifications = serviceNotification.getUnreadForUser(CurrentUser);
                if (unreadNotifications.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Aucune nouvelle notification.");
                    alert.showAndWait();
                } else {
                    StringBuilder message = new StringBuilder();
                    for (Notification n : unreadNotifications) {
                        message.append("- ").append(n.getMessage()).append("\n");
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, message.toString());
                    alert.setHeaderText("Notifications");
                    alert.showAndWait();

                    // 🔥 Mark all notifications as read
                    serviceNotification.markAllAsRead(CurrentUser);

                    // 🔥 Hide badge after viewing
                    notificationBadge.setVisible(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void startAutoRefreshBadge() {
            timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {

                    checkUnreadMessages();
                checkUnreadNotifications();
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }


        private void checkUnreadNotifications() {
            if (CurrentUser == null) return;

            ServiceNotification serviceNotification = new ServiceNotification();
            try {
                List<Notification> unreadNotifications = serviceNotification.getUnreadForUser(CurrentUser);
                Platform.runLater(() -> {
                    boolean shouldShow = !unreadNotifications.isEmpty();
                    if (shouldShow && !notificationBadge.isVisible()) {
                        playPopAnimation(notificationBadge); // 🔥 Bounce when new notification arrives
                    }
                    notificationBadge.setVisible(shouldShow);
                    notificationBadge.setText(shouldShow ? String.valueOf(unreadNotifications.size()) : "");
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        // ✨ Animation pop when badge appears
        private void playPopAnimation(Node node) {
            if (node == null) return;

            ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
            st.setFromX(0);
            st.setFromY(0);
            st.setToX(1);
            st.setToY(1);
            st.play();
        }
        private void playNotificationSound() {
            try {
                AudioClip clip = new AudioClip(getClass().getResource("/sounds/ping.mp3").toExternalForm());
                clip.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
