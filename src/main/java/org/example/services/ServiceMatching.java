
package org.example.services;

import org.example.entities.Matching;
import org.example.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceMatching implements IServices<Matching> {
    private final Connection connection;

    public ServiceMatching() {
        connection = MyDataBase.getInstance().getMyConnection();
    }

    @Override
    public void add(Matching matching) throws SQLException {
        String sql = "INSERT INTO matching (cin, description, date, competences, cv, price, availability) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, matching.getCin());
            pstmt.setString(2, matching.getDescription());
            pstmt.setDate(3, Date.valueOf(matching.getDate())); // assuming LocalDate
            pstmt.setString(4, matching.getCompetences());
            pstmt.setString(5, matching.getCvPath());
            pstmt.setDouble(6, matching.getPrice());
            pstmt.setBoolean(7, matching.isAvailability());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void update(Matching matching) throws SQLException {
        String sql = "UPDATE matching SET cin = ?, description = ?, date = ?, competences = ?, cv = ?, price = ?, availability = ? " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, matching.getCin());
            pstmt.setString(2, matching.getDescription());
            pstmt.setDate(3, Date.valueOf(matching.getDate()));
            pstmt.setString(4, matching.getCompetences());
            pstmt.setString(5, matching.getCvPath());
            pstmt.setDouble(6, matching.getPrice());
            pstmt.setBoolean(7, matching.isAvailability());
            pstmt.setInt(8, matching.getId()); // assumes Matching has getId()
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM matching WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Matching findById(int id) throws SQLException {
        // You can ask me to help you implement this when you're ready
        return null;
    }

    @Override
    public List<Matching> findAll() throws SQLException {
        List<Matching> matchings = new ArrayList<>();
        String sql = "SELECT * FROM matching";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Matching matching = new Matching(
                        rs.getInt("id"),
                        rs.getString("cin"),
                        rs.getString("description"),
                        rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null,
                        rs.getString("competences"),
                        rs.getString("cv"),
                        rs.getFloat("price"),  // Changed from getDouble to getFloat
                        rs.getBoolean("availability")
                );
                matchings.add(matching);
            }
        }
        return matchings;
    }

}