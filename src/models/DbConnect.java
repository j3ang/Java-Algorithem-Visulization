package models;


import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnect {
    // Code database URL
    static final String DB_URL = Dotenv.load().get("DB_URL");
    // Database credentials
    static final String USER = Dotenv.load().get("DB_USER"), PASS = Dotenv.load().get("DB_PASS");

    // Return Database Connection
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }


}
