package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TripDAO {

    public void addTrip(String title, String destination, String startDate, String endDate, int userId) {
        String sql = "INSERT INTO trips (title, destination, start_date, end_date, user_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, destination);
            ps.setDate(3, java.sql.Date.valueOf(startDate));
            ps.setDate(4, java.sql.Date.valueOf(endDate));
            ps.setInt(5, userId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getTripsByUser(int userId) throws Exception {
        Connection conn = DBConnection.getConnection();

        String sql = "SELECT * FROM trips WHERE user_id = ? ORDER BY start_date";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);

        return ps.executeQuery();
    }

    public String getTripTitle(int tripId) {
        String sql = "SELECT title FROM trips WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tripId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("title");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Unknown Trip";
    }
}