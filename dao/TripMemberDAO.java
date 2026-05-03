package dao;

import database.DBConnection;
import java.sql.*;

public class TripMemberDAO {

    public ResultSet getUserFriends(int userId) throws Exception {
        Connection conn = DBConnection.getConnection();

        String sql = "SELECT u.id, u.username FROM friends f " +
                "JOIN users u ON f.friend_id = u.id " +
                "WHERE f.user_id = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);

        return ps.executeQuery();
    }

    public void addMemberToTrip(int tripId, int userId) {
        String sql = "INSERT INTO trip_members (trip_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tripId);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getTripMembers(int tripId) throws Exception {
        Connection conn = DBConnection.getConnection();

        String sql = "SELECT u.username FROM trip_members tm " +
                "JOIN users u ON tm.user_id = u.id " +
                "WHERE tm.trip_id = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, tripId);

        return ps.executeQuery();
    }

    public void leaveTrip(int tripId, int userId) {
        String sql = "DELETE FROM trip_members WHERE trip_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tripId);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isMember(int tripId, int userId) {
        String sql = "SELECT 1 FROM trip_members WHERE trip_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tripId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}