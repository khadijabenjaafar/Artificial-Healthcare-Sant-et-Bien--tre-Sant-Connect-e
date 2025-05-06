package org.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.services.ServiceChat;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.example.entities.MessageChat;
import org.example.services.ServiceChat;

import java.time.LocalDateTime;
import java.util.ResourceBundle;


public class ChatbotController implements Initializable {

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField userInput;

    @FXML
    private ScrollPane chatScrollPane;

    private final ServiceChat messageService = new ServiceChat();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            for (MessageChat message : messageService.afficher()) {
                String prefix = message.getType().equals("user") ? "👤 Vous: " : "🤖 : ";
                chatArea.appendText(prefix + message.getContenu() + "\n");
            }
            chatScrollPane.setVvalue(1.0); // pour scroller tout en bas
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSendMessage() {
        String userMessage = userInput.getText().trim();
        if (userMessage.isEmpty()) return;

        chatArea.appendText("👤 Vous: " + userMessage + "\n");
        userInput.clear();

        // 🔸 Enregistrer le message utilisateur
        try {
            messageService.ajouter(new MessageChat(userMessage,"user", LocalDateTime.now()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Afficher un message de chargement
        chatArea.appendText("Chargement...\n");
        chatScrollPane.setVvalue(1.0); // Scroll vers le bas pour voir la réponse

        new Thread(() -> {
            String botReply = sendToOllama(userMessage);
            // 🔸 Enregistrement de la réponse bot
            try {
                messageService.ajouter(new MessageChat(botReply,"bot", LocalDateTime.now()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                // Remplacer le message de chargement par la réponse réelle du bot
                String lastMessage = chatArea.getText();
                int lastMessageIndex = lastMessage.lastIndexOf("Chargement...");
                if (lastMessageIndex != -1) {
                    chatArea.replaceText(lastMessageIndex, lastMessageIndex + "Chargement...".length(), "🤖 : " + botReply);
                }
                chatScrollPane.setVvalue(1.0); // Scroll vers le bas pour voir la réponse
            });
        }).start();
    }

    private String sendToOllama(String prompt) {
        try {
            URL url = new URL("http://localhost:11434/api/chat");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            // Format request body with system and user message
            String requestBody = "{"
                    + "\"model\": \"llama3.2:3b\","
                    + "\"stream\": true,"
                    + "\"messages\": ["
                    + "{\"role\": \"system\", \"content\": \"Tu es un assistant utile.\"},"
                    + "{\"role\": \"user\", \"content\": \"" + escapeJson(prompt) + "\"}"
                    + "]"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() != 200) {
                return "Erreur: " + conn.getResponseCode();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder fullReply = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    JSONObject json = new JSONObject(line);
                    if (json.has("message")) {
                        JSONObject message = json.getJSONObject("message");
                        fullReply.append(message.getString("content"));
                    }
                    if (json.has("done") && json.getBoolean("done")) {
                        break;
                    }
                }
            }

            return fullReply.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Impossible de se connecter à Ollama.";
        }
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
