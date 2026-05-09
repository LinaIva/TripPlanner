package servlets;

import dao.FriendDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.ResultSet;
import util.PageRenderer;

@WebServlet("/friends")
public class FriendsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.html");
            return;
        }
        int userId = (int) session.getAttribute("userId");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        PageRenderer.renderPageStart(out, session, "Friends", "friends");
        out.println("<h2>Friends</h2>");
        out.println("<form action='friends' method='post'>");
        out.println("Search username: <input type='text' name='search'>");
        out.println("<input type='submit' value='Search'>");
        out.println("</form>");
        out.println("<br><h3>Your friends</h3>");
        try {
            FriendDAO dao = new FriendDAO();
            ResultSet friends = dao.getFriends(userId);
            while (friends.next()) {
                if (friends.isFirst()) out.println("<ul>");
                out.println("<li>" + friends.getString("username") + "</li>");
            }
            if (friends.isBeforeFirst()) out.println("<p>No friends yet. Search for someone above to get started.</p>");
            else out.println("</ul>");
        } catch (Exception e) {
            out.println("<p>Couldn't load your friends right now.</p>");
            e.printStackTrace();
        }
        out.println("<br><a href='friend-requests'>Friend requests</a>");
        out.println("<br><a href='trips'>Back to my trips</a>");
        PageRenderer.renderPageEnd(out);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.html");
            return;
        }
        int userId = (int) session.getAttribute("userId");
        String search = request.getParameter("search");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        PageRenderer.renderPageStart(out, session, "Friends", "friends");
        out.println("<h2>Search results</h2>");
        try {
            FriendDAO dao = new FriendDAO();
            ResultSet rs = dao.findUsers(search, userId);
            boolean found = false;
            while (rs.next()) {
                found = true;
                int foundUserId = rs.getInt("id");
                String username = rs.getString("username");
                out.println("<p>" + username + " <a href='send-friend-request?receiverId=" + foundUserId + "'>Add friend</a></p>");
            }
            if (!found) out.println("<p>No matching users found. Try another username.</p>");
        } catch (Exception e) {
            out.println("<p>Couldn't search for users right now.</p>");
            e.printStackTrace();
        }
        out.println("<br><a href='friends'>Back to friends</a>");
        PageRenderer.renderPageEnd(out);
    }
}
