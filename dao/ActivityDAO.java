package dao;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ActivityDAO {

    public void addActivity(int tripId, String activityDate, String type,
                            String title, String description, double price) {

        String sql = "INSERT INTO activities (trip_id, activity_date, type, title, description, price) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tripId);
            ps.setDate(2, java.sql.Date.valueOf(activityDate));
            ps.setString(3, type);
            ps.setString(4, title);
            ps.setString(5, description);
            ps.setDouble(6, price);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getActivitiesByTrip(int tripId) throws Exception {
        Connection conn = DBConnection.getConnection();

        String sql = "SELECT * FROM activities WHERE trip_id = ? ORDER BY activity_date";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, tripId);

        return ps.executeQuery();
    }
}