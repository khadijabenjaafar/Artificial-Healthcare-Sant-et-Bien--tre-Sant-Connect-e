package org.example.services;

import org.example.entities.Notification;
import org.example.entities.Utilisateur;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceNotification {

    public void add(Notification notification) throws SQLException {
        Connection con = MyDataBase.getInstance().getMyConnection();
        String query = "INSERT INTO notification (message, is_read, receiver_id) VALUES (?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, notification.getMessage());
        ps.setBoolean(2, false);
        ps.setInt(3, notification.getReceiver().getId());
        ps.executeUpdate();
    }

    public List<Notification> getUnreadForUser(Utilisateur user) throws SQLException {
        Connection con = MyDataBase.getInstance().getMyConnection();  // Correction ici aussi
        String query = "SELECT * FROM notification WHERE receiver_id = ? AND is_read = false";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, user.getId());
        ResultSet rs = ps.executeQuery();

        List<Notification> notifications = new ArrayList<>();
        while (rs.next()) {
            Notification n = new Notification();
            n.setId(rs.getLong("id"));
            n.setMessage(rs.getString("message"));

            notifications.add(n);
        }
        return notifications;
    }
    public void markAllAsRead(Utilisateur user) throws SQLException {
        Connection con = MyDataBase.getInstance().getMyConnection();
        String query = "UPDATE notification SET is_read = true WHERE receiver_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, user.getId());
        ps.executeUpdate();
    }

}
