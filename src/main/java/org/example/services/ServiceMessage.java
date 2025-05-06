package org.example.services;

import org.example.entities.Message;
import org.example.entities.Utilisateur;
import org.example.utils.MyDataBase;

import java.security.cert.Extension;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceMessage {
    private Utilisateur currentUser;
    private Utilisateur selectedUser;
    private static Connection connection = MyDataBase.getInstance().getMyConnection();



    public ServiceMessage(){

        connection = MyDataBase.getInstance().getMyConnection();
    }
    public void save(Message message) {
        String sql = "INSERT INTO message_socket (sender_id, receiver_id, content, created_at, is_read) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, message.getSenderId());
            ps.setInt(2, message.getReceiverId());
            ps.setString(3, message.getContent());

            // Fix: Handle possible null created_at
            LocalDateTime createdAt = message.getCreated_at() != null ? message.getCreated_at() : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(createdAt));

            ps.setBoolean(5, false); // default to unread
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Message> getConversation(int senderId, int receiverId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message_socket WHERE " +
                "(sender_id = ? AND receiver_id = ?) OR " +
                "(sender_id = ? AND receiver_id = ?) " +
                "ORDER BY created_at";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setInt(3, receiverId);
            ps.setInt(4, senderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {



                Message msg = new Message();
                msg.setId(rs.getInt("id"));
                msg.setSenderId(rs.getInt("sender_id"));
                msg.setReceiverId(rs.getInt("receiver_id"));
                msg.setContent(rs.getString("content"));
                msg.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
                msg.setRead(rs.getBoolean("is_read"));
                messages.add(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public void markMessagesAsRead(int currentUserId, int senderId) {
        String sql = "UPDATE message_socket SET is_read = true WHERE receiver_id = ? AND sender_id = ? AND is_read = false";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, senderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean hasUnreadMessages(int receiverId) {
        String sql = "SELECT COUNT(*) FROM message_socket WHERE receiver_id = ? AND is_read = false";
        try ( PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, receiverId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Unread messages count: " + count); // Debug log
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking unread messages: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public int getUnreadCount(int receiverId) {
        String sql = "SELECT COUNT(*) FROM message_socket WHERE receiver_id = ? AND is_read = false";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, receiverId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
