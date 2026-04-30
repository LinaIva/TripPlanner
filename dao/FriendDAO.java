package dao;

import database.DBConnection;
import java.sql.*;

public class FriendDAO {

    public ResultSet findUsers(String username, int currentUserId) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT id, username FROM users WHERE username ILIKE ? AND id <> ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + username + "%");
        ps.setInt(2, currentUserId);
        return ps.executeQuery();
    }

    public void sendRequest(int senderId, int receiverId) {
        String sql = "INSERT INTO friend_requests (sender_id, receiver_id, status) VALUES (?, ?, 'WAITING')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getWaitingRequests(int userId) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT fr.id, u.username FROM friend_requests fr JOIN users u ON fr.sender_id = u.id WHERE fr.receiver_id = ? AND fr.status = 'WAITING'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        return ps.executeQuery();
    }

    public void acceptRequest(int requestId) {
        String selectSql = "SELECT sender_id, receiver_id FROM friend_requests WHERE id = ?";
        String updateSql = "UPDATE friend_requests SET status = 'ACCEPTED' WHERE id = ?";
        String insertSql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?), (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement select = conn.prepareStatement(selectSql);
            select.setInt(1, requestId);
            ResultSet rs = select.executeQuery();

            if (rs.next()) {
                int senderId = rs.getInt("sender_id");
                int receiverId = rs.getInt("receiver_id");

                PreparedStatement update = conn.prepareStatement(updateSql);
                update.setInt(1, requestId);
                update.executeUpdate();

                PreparedStatement insert = conn.prepareStatement(insertSql);
                insert.setInt(1, senderId);
                insert.setInt(2, receiverId);
                insert.setInt(3, receiverId);
                insert.setInt(4, senderId);
                insert.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void declineRequest(int requestId) {
        String sql = "UPDATE friend_requests SET status = 'DECLINED' WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet getFriends(int userId) throws Exception {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT u.username FROM friends f JOIN users u ON f.friend_id = u.id WHERE f.user_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        return ps.executeQuery();
    }
}