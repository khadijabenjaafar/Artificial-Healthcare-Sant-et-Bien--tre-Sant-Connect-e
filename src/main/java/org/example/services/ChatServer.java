package org.example.services;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("✅ Chat Server started on port " + PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler client = new ClientHandler(clientSocket);
            clients.add(client);
            new Thread(client).start();
        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(message);
                }
            }
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter writer;
        private BufferedReader reader;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void sendMessage(String message) {
            writer.println(message);
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                // 🔒 Première ligne = nom de l'utilisateur
                writer.println("Entrez votre nom :");
                username = reader.readLine();
                System.out.println("👤 Connecté : " + username);

                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("📨 Reçu de " + username + ": " + message);
                    ChatServer.broadcast(username + ": " + message, this);
                }
            } catch (IOException e) {
                System.out.println("❌ Déconnexion de " + username);
            }
        }
    }
}
