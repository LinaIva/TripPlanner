package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() throws SQLException {
        String url = EnvConfig.get("DB_URL");
        String user = EnvConfig.get("DB_USER");
        String password = EnvConfig.get("DB_PASSWORD");
        if (url == null || user == null || password == null) {
            throw new SQLException("Missing DB configuration. Please set DB_URL, DB_USER, and DB_PASSWORD in environment variables or .env.");
        }
        return DriverManager.getConnection(url, user, password);
    }
}
