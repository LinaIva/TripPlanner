package dao;

import database.DBConnection;
import java.sql.*;

public class ChatMessageDAO {

    public void saveMessage(int tripId, String username, String message) {
        String sql = "INSERT INTO chat_messages (trip_id, username, message) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tripId);
            ps.setString(2, username);
            ps.setString(3, message);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getLastMessages(int tripId) throws Exception {
        Connection conn = DBConnection.getConnection();

        String sql =
                "SELECT username, message, created_at FROM chat_messages " +
                        "WHERE trip_id = ? ORDER BY created_at DESC LIMIT 50";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, tripId);

        return ps.executeQuery();
    }
}