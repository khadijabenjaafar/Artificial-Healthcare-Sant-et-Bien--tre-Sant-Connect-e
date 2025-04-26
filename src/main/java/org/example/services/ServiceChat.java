package org.example.services;

import org.example.entities.MessageChat;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceChat implements IService<MessageChat> {

    private Connection connection;
    public ServiceChat(){
        connection= MyDataBase.getInstance().getMyConnection();
    }
    @Override
    public void ajouter(MessageChat msg) throws SQLException {
        String req = "INSERT INTO message_chat (contenu, type, date_heure) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, msg.getContenu());
        ps.setString(2, msg.getType());
        ps.setTimestamp(3, Timestamp.valueOf(msg.getDateHeure()));
        ps.executeUpdate();
    }

    @Override
    public void modifier(MessageChat msg) throws SQLException {

    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    @Override
    public List<MessageChat> afficher() throws SQLException {
        List<MessageChat> liste = new ArrayList<>();
        String req = "SELECT * FROM message_chat ORDER BY date_heure";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            MessageChat msg = new MessageChat();
            msg.setId(rs.getInt("id"));
            msg.setContenu(rs.getString("contenu"));
            msg.setType(rs.getString("type"));
            msg.setDateHeure(rs.getTimestamp("date_heure").toLocalDateTime());
            liste.add(msg);
        }
        return liste;
    }
}
