package org.example.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.entities.Message;
import org.example.entities.UserConnecter;
import org.example.entities.Utilisateur;
import org.example.services.ServiceMessage;
import org.example.services.ServiceUtilisateur;

import java.time.LocalDateTime;
import java.util.List;

public class ChatController {

    @FXML
    private ListView<Utilisateur> userListView;

    @FXML
    private VBox chatBox;

    @FXML
    private ScrollPane chatScroll;

    @FXML
    private TextField chatInput;

    @FXML
    private Button sendButton;

    private Utilisateur currentUser;
    private Utilisateur selectedUser;

    private final ServiceUtilisateur userService = new ServiceUtilisateur();
    private final ServiceMessage messageService = new ServiceMessage();

    @FXML
    public void initialize() {
        currentUser = UserConnecter.getInstance().getUserConnecter();
        loadUsers();

        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedUser = newVal;
            if (selectedUser != null) {
                loadConversation(selectedUser);
            }
        });
    }

    private void loadUsers() {
        List<Utilisateur> users = userService.getAllUsersExcept(currentUser.getId());
        userListView.setItems(FXCollections.observableArrayList(users));

        userListView.setCellFactory(lv -> new ListCell<>() {
            private final HBox content = new HBox(10);
            private final ImageView avatar = new ImageView();
            private final Text name = new Text();

            {
                content.getChildren().addAll(avatar, name);
                avatar.setFitWidth(36);
                avatar.setFitHeight(36);
                avatar.setPreserveRatio(true);
                avatar.setSmooth(true);
                avatar.setStyle("-fx-border-radius: 50%; -fx-background-radius: 50%;");
            }

            @Override
            protected void updateItem(Utilisateur user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setGraphic(null);
                } else {
                    name.setText(user.getPrenom() + " " + user.getNom());
                    String imagePath = user.getImage1() != null ? user.getImage1() : "/img/avatar.png";
                    try {
                        avatar.setImage(new Image(imagePath, true));
                    } catch (Exception e) {
                        avatar.setImage(new Image("/img/avatar.png")); // fallback
                    }
                    setGraphic(content);
                }
            }
        });
    }
    private void loadConversation(Utilisateur receiver) {
        if (receiver == null || currentUser == null) return;

        chatBox.getChildren().clear();
        List<Message> messages = messageService.getConversation(currentUser.getId(), receiver.getId());
        for (Message msg : messages) {
            boolean isSender = msg.getSenderId() == currentUser.getId();
            addMessageBubbleWithTick(msg.getContent(), isSender, msg.isRead());
        }
        messageService.markMessagesAsRead(currentUser.getId(), receiver.getId());
    }


    private void addMessageBubble(String content, boolean isSender) {
        Label label = new Label(content);
        label.setWrapText(true);
        label.setStyle("-fx-padding: 10; -fx-background-radius: 10; -fx-font-size: 14px;" +
                (isSender ? "-fx-background-color: #DCF8C6; -fx-alignment: CENTER_RIGHT;" : "-fx-background-color: #E8E8E8;"));

        HBox bubble = new HBox(label);
        bubble.setPadding(new javafx.geometry.Insets(5));
        bubble.setStyle("-fx-alignment: " + (isSender ? "TOP_RIGHT" : "TOP_LEFT"));

        Platform.runLater(() -> {
            chatBox.getChildren().add(bubble);
            chatScroll.setVvalue(1.0);
        });
    }
    @FXML private ListView<HBox> chatListView;

    @FXML
    private void handleSend() {
        String messageText = chatInput.getText();
        if (messageText == null || messageText.trim().isEmpty() || selectedUser == null) {
            return;
        }

        Message msg = new Message();
        msg.setSenderId(currentUser.getId());
        msg.setReceiverId(selectedUser.getId());
        msg.setContent(messageText);
        msg.setCreated_at(LocalDateTime.now());
        msg.setRead(false);

        messageService.save(msg);

        addMessageBubbleWithTick(messageText, true, false);
        chatInput.clear();
    }

    private HBox createMessageBubble(String sender, String content, boolean isMe, boolean isRead) {
        Label messageLabel = new Label(content);
        Label statusTick = new Label();

        if (isMe) {
            statusTick.setText(isRead ? "\u2713\u2713" : "\u2713"); // ✓✓ ou ✓
            statusTick.setStyle(isRead ? "-fx-text-fill: #1ca7a2;" : "-fx-text-fill: grey;");
        }

        HBox messageBox = new HBox();
        messageBox.setSpacing(5);
        messageBox.setStyle("-fx-padding: 5;");

        if (isMe) {
            messageBox.getChildren().addAll(messageLabel, statusTick);
            messageBox.setStyle("-fx-alignment: CENTER_RIGHT; -fx-background-color: #DCF8C6; -fx-background-radius: 10;");
        } else {
            messageBox.getChildren().addAll(new Label(sender + ": "), messageLabel);
            messageBox.setStyle("-fx-alignment: CENTER_LEFT; -fx-background-color: #ffffff; -fx-background-radius: 10;");
        }

        return messageBox;
    }
    private void addMessageBubbleWithTick(String content, boolean isSender, boolean isRead) {
        if (content == null) return;

        Label label = new Label(content);
        label.setWrapText(true);

        Label tick = new Label();
        if (isSender) {
            tick.setText(isRead ? "\u2713\u2713" : "\u2713");
            tick.setStyle(isRead ? "-fx-text-fill: #1ca7a2;" : "-fx-text-fill: grey;");
        }

        HBox bubble = new HBox();
        bubble.setSpacing(5);
        bubble.setPadding(new Insets(5));

        if (isSender) {
            bubble.setAlignment(Pos.CENTER_RIGHT);
            bubble.setStyle("-fx-background-color: #DCF8C6; -fx-background-radius: 10;");
            bubble.getChildren().addAll(label, tick);
        } else {
            bubble.setAlignment(Pos.CENTER_LEFT);
            bubble.setStyle("-fx-background-color: #E8E8E8; -fx-background-radius: 10;");
            bubble.getChildren().add(label);
        }

        Platform.runLater(() -> {
            if (chatBox != null) {
                chatBox.getChildren().add(bubble);
                if (chatScroll != null) {
                    chatScroll.setVvalue(1.0);
                }
            }
        });
    }

}