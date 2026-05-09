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
            out.println("<ul>");
            while (friends.next()) {
                out.println("<li>" + friends.getString("username") + "</li>");
            }
            out.println("</ul>");
        } catch (Exception e) {
            out.println("<p>Error loading friends</p>");
            e.printStackTrace();
        }
        out.println("<br><a href='friend-requests'>Friend requests</a>");
        out.println("<br><a href='trips'>Back to trips</a>");
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
            while (rs.next()) {
                int foundUserId = rs.getInt("id");
                String username = rs.getString("username");
                out.println("<p>" + username + " <a href='send-friend-request?receiverId=" + foundUserId + "'>Add friend</a></p>");
            }
        } catch (Exception e) {
            out.println("<p>Error searching users</p>");
            e.printStackTrace();
        }
        out.println("<br><a href='friends'>Back</a>");
        PageRenderer.renderPageEnd(out);
    }
}
