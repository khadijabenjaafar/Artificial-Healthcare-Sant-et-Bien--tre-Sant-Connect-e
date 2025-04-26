package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {
    private final String URL = "jdbc:mysql://localhost:3306/pidev";
    private final String USER = "root";
    private final String PSW = "";

    private Connection myConnection;
    private static MyDataBase instance;

    private MyDataBase() {
        connect();
    }

    private void connect() {
        try {
            myConnection = DriverManager.getConnection(URL, USER, PSW);
            System.out.println("✅ Connected to database.");
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
        }
    }

    public static MyDataBase getInstance() {
        if (instance == null)
            instance = new MyDataBase();
        return instance;
    }

    public Connection getMyConnection() {
        try {
            if (myConnection == null || myConnection.isClosed()) {
                System.out.println("⚠️ Connection was closed. Reconnecting...");
                connect();
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Failed checking connection: " + e.getMessage());
        }
        return myConnection;
    }

}
