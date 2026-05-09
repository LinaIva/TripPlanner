package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TripDAO {

    public void addTrip(String title, String destination, String startDate, String endDate, int userId) {
        String tripSql = "INSERT INTO trips (title, destination, start_date, end_date, user_id) VALUES (?, ?, ?, ?, ?) RETURNING id";
        String memberSql = "INSERT INTO trip_members (trip_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement tripPs = conn.prepareStatement(tripSql)) {
            tripPs.setString(1, title);
            tripPs.setString(2, destination);
            tripPs.setDate(3, java.sql.Date.valueOf(startDate));
            tripPs.setDate(4, java.sql.Date.valueOf(endDate));
            tripPs.setInt(5, userId);
            ResultSet rs = tripPs.executeQuery();
            if (rs.next()) {
                int tripId = rs.getInt("id");
                try (PreparedStatement memberPs = conn.prepareStatement(memberSql)) {
                    memberPs.setInt(1, tripId);
                    memberPs.setInt(2, userId);
                    memberPs.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getTripsByUser(int userId) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT DISTINCT t.* FROM trips t LEFT JOIN trip_members tm ON t.id = tm.trip_id " +
                "WHERE t.user_id = ? OR tm.user_id = ? ORDER BY t.start_date";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, userId);
        return ps.executeQuery();
    }

    public String getTripTitle(int tripId) {
        String sql = "SELECT title FROM trips WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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